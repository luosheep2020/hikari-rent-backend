package com.hikari.rent.area.cache;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Slf4j
@Component
public class AreaPopularityCache {

  private static final String RANKING_KEY =
    "hikari:areas:popular:v1";

  @Resource(name = "stringRedisTemplate")
  private StringRedisTemplate redisTemplate;

  public void increment(String areaCode) {
    try {
      redisTemplate.opsForZSet()
        .incrementScore(RANKING_KEY, areaCode, 1D);
    } catch (DataAccessException ex) {
      // Popularity tracking must not interrupt property searches.
      log.warn(
        "Failed to increment area popularity: {}",
        ex.getMessage()
      );
    }
  }

  public List<String> getTopCodes(int limit) {
    if (limit <= 0) {
      return List.of();
    }

    try {
      Set<String> codes = redisTemplate.opsForZSet()
        .reverseRange(RANKING_KEY, 0, limit - 1L);

      return codes == null
        ? List.of()
        : List.copyOf(codes);
    } catch (DataAccessException ex) {
      // The service will use default areas when Redis is unavailable.
      log.warn(
        "Failed to read area popularity: {}",
        ex.getMessage()
      );

      return List.of();
    }
  }
}
