package com.hikari.rent.common.cache;

import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;

@Component

public class JsonCache {

  @Resource
  private StringRedisTemplate stringRedisTemplate;
  @Resource
  private JsonMapper jsonMapper;

  /**
   * Cache TTL must not be null
   */
  public void put(String key, Object value, Duration ttl) {
    validateKey(key);
    Objects.requireNonNull(value, "Cache value must not be null");
    Objects.requireNonNull(ttl, "Cache TTL must not be null");

    if (ttl.isZero() || ttl.isNegative()) {
      throw new IllegalArgumentException(
        "Cache TTL must be positive"
      );
    }

    String json = jsonMapper.writeValueAsString(value);

    stringRedisTemplate.opsForValue().set(key, json, ttl);
  }

  /**
   * get cache
   */
  public <T> Optional<T> get(String key, Class<T> type) {
    validateKey(key);
    Objects.requireNonNull(type, "Cache type must not be null");

    String json = stringRedisTemplate.opsForValue().get(key);

    if (json == null) {
      return Optional.empty();
    }

    return Optional.ofNullable(
      jsonMapper.readValue(json, type)
    );
  }


  public <T> Optional<T> get(
    String key,
    TypeReference<T> type
  ) {
    validateKey(key);
    Objects.requireNonNull(type, "Cache type must not be null");

    String json = stringRedisTemplate.opsForValue().get(key);

    if (json == null) {
      return Optional.empty();
    }

    return Optional.ofNullable(
      jsonMapper.readValue(json, type)
    );
  }

  /**
   * delete cache
   */
  public boolean delete(String key) {
    validateKey(key);

    return Boolean.TRUE.equals(
      stringRedisTemplate.delete(key)
    );
  }

  private void validateKey(String key) {
    if (key == null || key.isBlank()) {
      throw new IllegalArgumentException(
        "Cache key must not be blank"
      );
    }
  }
}
