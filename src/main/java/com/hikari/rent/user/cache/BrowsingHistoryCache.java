package com.hikari.rent.user.cache;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import com.hikari.rent.user.repository.model.BrowsingHistoryRow;

import jakarta.annotation.Resource;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

@Component
public class BrowsingHistoryCache {

  private static final int MAX_HISTORY_SIZE = 30;

  private static final String KEY_PREFIX =
    "hikari:{history}:";

  private static final String DIRTY_USERS_KEY =
    KEY_PREFIX + "dirty-users";

  /*
   * Initialize once from MySQL.
   * The state key also represents an initialized empty history.
   */
  private static final DefaultRedisScript<Long> INITIALIZE_SCRIPT =
    new DefaultRedisScript<>(
      """
      if redis.call('EXISTS', KEYS[2]) == 1 then
        return 0
      end

      redis.call('DEL', KEYS[1])

      for i = 1, #ARGV, 2 do
        redis.call(
          'ZADD',
          KEYS[1],
          ARGV[i + 1],
          ARGV[i]
        )
      end

      redis.call('ZREMRANGEBYRANK', KEYS[1], 0, -31)
      redis.call('SET', KEYS[2], '0')

      return 1
      """,
      Long.class
    );

  /*
   * Apply a mutation, increase the version, and mark the user dirty.
   * KEYS: history, version, dirty users.
   * ARGV: operation, rental ID, timestamp, user ID.
   */
  private static final DefaultRedisScript<Long> MUTATE_SCRIPT =
    new DefaultRedisScript<>(
      """
      if redis.call('EXISTS', KEYS[2]) == 0 then
        return -1
      end

      local operation = ARGV[1]

      if operation == 'VIEW' then
        local previous = redis.call(
          'ZSCORE',
          KEYS[1],
          ARGV[2]
        )

        if not previous
          or tonumber(ARGV[3]) > tonumber(previous) then

          redis.call(
            'ZADD',
            KEYS[1],
            ARGV[3],
            ARGV[2]
          )
        end

        redis.call(
          'ZREMRANGEBYRANK',
          KEYS[1],
          0,
          -31
        )

      elseif operation == 'REMOVE' then
        redis.call('ZREM', KEYS[1], ARGV[2])

      elseif operation == 'CLEAR' then
        redis.call('DEL', KEYS[1])

      else
        return redis.error_reply('Unknown history operation')
      end

      local version = redis.call('INCR', KEYS[2])

      redis.call(
        'SADD',
        KEYS[3],
        ARGV[4]
      )

      return version
      """,
      Long.class
    );

  /*
   * Read the version and records in one atomic operation.
   * An empty result means the cache has not been initialized.
   */
  @SuppressWarnings("rawtypes")
  private static final DefaultRedisScript<List> SNAPSHOT_SCRIPT =
    new DefaultRedisScript<>(
      """
      local version = redis.call('GET', KEYS[2])

      if not version then
        return {}
      end

      local result = {version}

      local records = redis.call(
        'ZREVRANGE',
        KEYS[1],
        0,
        29,
        'WITHSCORES'
      )

      for i = 1, #records do
        result[#result + 1] = records[i]
      end

      return result
      """,
      List.class
    );

  /*
   * Acknowledge only the exact version committed to MySQL.
   * Newer mutations must remain scheduled for synchronization.
   */
  private static final DefaultRedisScript<Long> ACKNOWLEDGE_SCRIPT =
    new DefaultRedisScript<>(
      """
      local currentVersion = redis.call('GET', KEYS[1])

      if currentVersion
        and currentVersion == ARGV[1] then

        redis.call(
          'SREM',
          KEYS[2],
          ARGV[2]
        )

        return 1
      end

      return 0
      """,
      Long.class
    );

  @Resource(name = "stringRedisTemplate")
  private StringRedisTemplate stringRedisTemplate;

  public record HistorySnapshot(
    long version,
    List<BrowsingHistoryRow> records
  ) {
  }

  public boolean initializeIfAbsent(
    Long userId,
    List<BrowsingHistoryRow> records
  ) {

    validatePositiveId(userId);

    Objects.requireNonNull(
      records,
      "History records must not be null"
    );

    if (records.size() > MAX_HISTORY_SIZE) {
      throw new IllegalArgumentException(
        "History must contain at most 30 records"
      );
    }

    List<String> arguments = new ArrayList<>();
    Set<Long> rentalIds = new HashSet<>();

    for (BrowsingHistoryRow record : records) {

      Objects.requireNonNull(
        record,
        "History record must not be null"
      );

      validatePositiveId(record.getRentalId());

      Objects.requireNonNull(
        record.getLastViewedAt(),
        "Last viewed time must not be null"
      );

      if (!rentalIds.add(record.getRentalId())) {
        throw new IllegalArgumentException(
          "History contains duplicate rental IDs"
        );
      }

      long timestamp = record.getLastViewedAt()
        .toInstant(ZoneOffset.UTC)
        .toEpochMilli();

      arguments.add(record.getRentalId().toString());
      arguments.add(Long.toString(timestamp));
    }

    Long result = stringRedisTemplate.execute(
      INITIALIZE_SCRIPT,
      List.of(
        historyKey(userId),
        versionKey(userId)
      ),
      arguments.toArray()
    );

    if (result == null) {
      throw new IllegalStateException(
        "Redis history initialization returned no result"
      );
    }

    return result == 1L;
  }

  public Optional<HistorySnapshot> readSnapshot(
    Long userId
  ) {

    validatePositiveId(userId);

    List<?> result = stringRedisTemplate.execute(
      SNAPSHOT_SCRIPT,
      List.of(
        historyKey(userId),
        versionKey(userId)
      )
    );

    if (result == null) {
      throw new IllegalStateException(
        "Redis history read returned no result"
      );
    }

    if (result.isEmpty()) {
      return Optional.empty();
    }

    long version = Long.parseLong(
      result.getFirst().toString()
    );

    List<BrowsingHistoryRow> records =
      new ArrayList<>();

    for (int i = 1; i < result.size(); i += 2) {

      Long rentalId = Long.valueOf(
        result.get(i).toString()
      );

      long timestamp = new BigDecimal(
        result.get(i + 1).toString()
      ).longValueExact();

      BrowsingHistoryRow record =
        new BrowsingHistoryRow();

      record.setRentalId(rentalId);

      record.setLastViewedAt(
        LocalDateTime.ofInstant(
          Instant.ofEpochMilli(timestamp),
          ZoneOffset.UTC
        )
      );

      records.add(record);
    }

    return Optional.of(
      new HistorySnapshot(
        version,
        List.copyOf(records)
      )
    );
  }

  public void recordView(
    Long userId,
    Long rentalId
  ) {

    validatePositiveId(rentalId);

    mutate(
      userId,
      "VIEW",
      rentalId.toString(),
      System.currentTimeMillis()
    );
  }

  public void remove(
    Long userId,
    Long rentalId
  ) {

    validatePositiveId(rentalId);

    mutate(
      userId,
      "REMOVE",
      rentalId.toString(),
      0L
    );
  }

  public void clear(Long userId) {

    mutate(
      userId,
      "CLEAR",
      "",
      0L
    );
  }

  public Set<Long> findDirtyUserIds() {

    Set<String> members = stringRedisTemplate
      .opsForSet()
      .members(DIRTY_USERS_KEY);

    if (members == null || members.isEmpty()) {
      return Set.of();
    }

    Set<Long> userIds = new HashSet<>();

    for (String member : members) {
      userIds.add(Long.valueOf(member));
    }

    return Set.copyOf(userIds);
  }

  public boolean acknowledge(
    Long userId,
    long savedVersion
  ) {

    validatePositiveId(userId);

    Long result = stringRedisTemplate.execute(
      ACKNOWLEDGE_SCRIPT,
      List.of(
        versionKey(userId),
        DIRTY_USERS_KEY
      ),
      Long.toString(savedVersion),
      userId.toString()
    );

    if (result == null) {
      throw new IllegalStateException(
        "Redis history acknowledgment returned no result"
      );
    }

    return result == 1L;
  }

  private void mutate(
    Long userId,
    String operation,
    String rentalId,
    long timestamp
  ) {

    validatePositiveId(userId);

    Long result = stringRedisTemplate.execute(
      MUTATE_SCRIPT,
      List.of(
        historyKey(userId),
        versionKey(userId),
        DIRTY_USERS_KEY
      ),
      operation,
      rentalId,
      Long.toString(timestamp),
      userId.toString()
    );

    if (result == null) {
      throw new IllegalStateException(
        "Redis history mutation returned no result"
      );
    }

    if (result == -1L) {
      throw new IllegalStateException(
        "History cache must be initialized before mutation"
      );
    }
  }

  private String historyKey(Long userId) {
    return KEY_PREFIX + "user:" + userId + ":records";
  }

  private String versionKey(Long userId) {
    return KEY_PREFIX + "user:" + userId + ":version";
  }

  private void validatePositiveId(Long id) {

    if (id == null || id <= 0) {
      throw new IllegalArgumentException(
        "ID must be positive"
      );
    }
  }
}
