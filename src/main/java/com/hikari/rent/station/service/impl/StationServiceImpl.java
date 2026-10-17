package com.hikari.rent.station.service.impl;

import com.hikari.rent.station.cache.StationTreeCache;
import com.hikari.rent.station.dto.StationSearchRequest;
import com.hikari.rent.station.repository.StationRepository;
import com.hikari.rent.station.repository.model.StationTreeRow;
import com.hikari.rent.station.service.StationService;
import com.hikari.rent.station.vo.StationOptionVO;
import com.hikari.rent.station.vo.StationSearchVO;
import com.hikari.rent.station.vo.StationTreeVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@Service
public class StationServiceImpl implements StationService {

  @Resource
  private StationRepository stationRepository;
  @Resource
  private StationTreeCache stationTreeCache;

  @Override
  @Transactional(readOnly = true)
  public List<StationSearchVO> search(
    StationSearchRequest request
  ) {
    String keyword = request.getKeyword();

    // Do not load stations before the user enters a keyword.
    if (keyword == null || keyword.isBlank()) {
      return List.of();
    }

    return stationRepository.search(
        keyword.trim(),
        request.getPrefectureCode(),
        request.getLimit()
      )
      .stream()
      .map(row -> new StationSearchVO(
        row.getId().toString(),
        row.getName(),
        row.getLineName(),
        row.getPrefectureCode(),
        row.getAreaCode(),
        row.getLatitude(),
        row.getLongitude()
      ))
      .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public List<StationOptionVO> getOperators() {
    return stationRepository.findOperators();
  }

  @Override
  @Transactional(readOnly = true)
  public List<StationOptionVO> getLines(
    String operatorCode
  ) {
    if (operatorCode == null || operatorCode.isBlank()) {
      return List.of();
    }

    return stationRepository.findLinesByOperatorCode(
      operatorCode.trim()
    );
  }

  @Override
  @Transactional(readOnly = true)
  public List<StationOptionVO> getStationOptions(
    Long lineId
  ) {
    if (lineId == null || lineId <= 0) {
      return List.of();
    }

    return stationRepository.findStationsByLineId(
      lineId
    );
  }

  @Override
  public List<StationTreeVO> getTree() {
    var cached = stationTreeCache.get();

    if (cached.isPresent()) {
      return cached.get();
    }

    List<StationTreeVO> tree = loadTreeFromDatabase();

    stationTreeCache.put(tree);

    return tree;
  }

  private List<StationTreeVO> loadTreeFromDatabase() {
    List<StationTreeRow> rows =
      stationRepository.findTreeRows();

    Map<String, StationTreeVO> operators =
      new LinkedHashMap<>();

    Map<String, StationTreeVO> lines =
      new LinkedHashMap<>();

    for (StationTreeRow row : rows) {
      StationTreeVO operator = operators.computeIfAbsent(
        row.getOperatorCode(),
        code -> createTreeNode(
          code,
          row.getOperatorName()
        )
      );

      StationTreeVO line = lines.get(row.getLineId());

      if (line == null) {
        line = createTreeNode(
          row.getLineId(),
          row.getLineName()
        );

        lines.put(row.getLineId(), line);
        operator.getChildren().add(line);
      }

      StationTreeVO station = createTreeNode(
        row.getStationId(),
        row.getStationName()
      );

      line.getChildren().add(station);
    }

    return new ArrayList<>(operators.values());
  }

  private StationTreeVO createTreeNode(
    String value,
    String label
  ) {
    StationTreeVO node = new StationTreeVO();

    node.setValue(value);
    node.setLabel(label);

    return node;
  }
}
