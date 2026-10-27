package com.hikari.rent.railway.cache;

import com.hikari.rent.common.cache.JsonCache;
import com.hikari.rent.railway.vo.StationTreeVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class StationTreeCache {

  private static final String KEY =
    "hikari:stations:tree:v1";

  private static final Duration TTL =
    Duration.ofHours(24);

  private static final TypeReference<List<StationTreeVO>> TREE_TYPE =
    new TypeReference<>() {};

  @Resource
  private JsonCache jsonCache;

  public Optional<List<StationTreeVO>> get() {
    try {
      return jsonCache.get(KEY, TREE_TYPE);
    } catch (RuntimeException exception) {
      log.warn(
        "Failed to read station tree cache: {}",
        exception.getMessage()
      );

      return Optional.empty();
    }
  }

  public void put(List<StationTreeVO> tree) {
    try {
      jsonCache.put(KEY, tree, TTL);
    } catch (RuntimeException exception) {
      log.warn(
        "Failed to write station tree cache: {}",
        exception.getMessage()
      );
    }
  }

  public void evict() {
    // Propagate eviction failures to the caller.
    jsonCache.delete(KEY);
  }
}
