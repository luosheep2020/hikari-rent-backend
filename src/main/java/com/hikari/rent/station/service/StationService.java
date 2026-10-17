package com.hikari.rent.station.service;

import com.hikari.rent.station.dto.StationSearchRequest;
import com.hikari.rent.station.vo.StationOptionVO;
import com.hikari.rent.station.vo.StationSearchVO;
import com.hikari.rent.station.vo.StationTreeVO;

import java.util.List;

public interface StationService {

  List<StationSearchVO> search(StationSearchRequest request);

  List<StationOptionVO> getOperators();

  List<StationOptionVO> getLines(String operatorCode);

  List<StationOptionVO> getStationOptions(Long lineId);

  List<StationTreeVO> getTree();
}
