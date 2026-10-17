package com.hikari.rent.area.cache;

import com.hikari.rent.area.vo.AreaTreeVO;
import com.hikari.rent.common.cache.JsonCache;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class AreaTreeCache {

  private static final String KEY =
    "hikari:areas:tree:v1";

  private static final Duration TTL =
    Duration.ofHours(24);

  private static final TypeReference<List<AreaTreeVO>> TREE_TYPE =
    new TypeReference<>() {};

  @Resource
  private  JsonCache jsonCache;

  public Optional<List<AreaTreeVO>> get() {
    try {
      return jsonCache.get(KEY, TREE_TYPE);
    } catch (RuntimeException exception) {
      log.warn(
        "Failed to read area tree cache: {}",
        exception.getMessage()
      );

      return Optional.empty();
    }
  }

  public void put(List<AreaTreeVO> tree) {
    try {
      jsonCache.put(KEY, tree, TTL);
    } catch (RuntimeException exception) {
      log.warn(
        "Failed to write area tree cache: {}",
        exception.getMessage()
      );
    }
  }

  public void evict() {
    jsonCache.delete(KEY);
  }
}
