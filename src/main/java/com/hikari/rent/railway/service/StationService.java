package com.hikari.rent.railway.service;

import com.hikari.rent.railway.dto.StationSearchRequest;
import com.hikari.rent.railway.vo.StationOptionVO;
import com.hikari.rent.railway.vo.StationSearchVO;
import com.hikari.rent.railway.vo.StationTreeVO;

import java.util.List;

public interface StationService {

  List<StationSearchVO> search(StationSearchRequest request);

  List<StationOptionVO> getOperators();

  List<StationOptionVO> getLines(String operatorCode);

  List<StationOptionVO> getStationOptions(Long lineId);

  List<StationTreeVO> getTree();
}
