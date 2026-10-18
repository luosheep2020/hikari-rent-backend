package com.hikari.rent.area.service.impl;

import com.hikari.rent.area.cache.AreaPopularityCache;
import com.hikari.rent.area.repository.AreaRepository;
import com.hikari.rent.area.repository.model.AreaPopularityRow;
import com.hikari.rent.area.service.AreaPopularityService;
import com.hikari.rent.area.vo.PopularAreaVO;

import com.hikari.rent.common.enums.ErrorCode;
import com.hikari.rent.common.exception.BusinessException;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AreaPopularityServiceImpl
  implements AreaPopularityService {

  private static final int DISPLAY_LIMIT = 4;

  // Read extra candidates to tolerate outdated Redis members.
  private static final int CANDIDATE_LIMIT = 20;

  private static final List<String> DEFAULT_AREA_CODES =
    List.of(
      "13104",
      "13113",
      "13114",
      "13203"
    );

  @Resource
  private AreaPopularityCache areaPopularityCache;

  @Resource
  private AreaRepository areaRepository;

  @Override
  @Transactional(readOnly = true)
  public List<PopularAreaVO> getPopularAreas() {
    List<String> rankedCodes =
      areaPopularityCache.getTopCodes(CANDIDATE_LIMIT);

    LinkedHashSet<String> candidateCodes =
      new LinkedHashSet<>(rankedCodes);

    candidateCodes.addAll(DEFAULT_AREA_CODES);

    List<AreaPopularityRow> rows =
      areaRepository.findPopularityAreasByCodes(
        List.copyOf(candidateCodes)
      );

    Map<String, AreaPopularityRow> areasByCode =
      rows.stream()
        .collect(Collectors.toMap(
          AreaPopularityRow::getCode,
          Function.identity()
        ));

    // Preserve Redis ranking; append defaults only when needed.
    return candidateCodes.stream()
      .map(areasByCode::get)
      .filter(Objects::nonNull)
      .limit(DISPLAY_LIMIT)
      .map(area -> new PopularAreaVO(
        area.getCode(),
        area.getName()
      ))
      .toList();
  }

  @Override
  public void recordSearch(String areaCode) {
    if (
      areaCode == null ||
        !areaCode.matches("[0-9]{5}")
    ) {
      throw new BusinessException(
        ErrorCode.BAD_REQUEST
      );
    }

    List<AreaPopularityRow> rows =
      areaRepository.findPopularityAreasByCodes(
        List.of(areaCode)
      );

    if (rows.isEmpty()) {
      throw new BusinessException(
        ErrorCode.AREA_NOT_FOUND
      );
    }

    areaPopularityCache.increment(areaCode);
  }
}
