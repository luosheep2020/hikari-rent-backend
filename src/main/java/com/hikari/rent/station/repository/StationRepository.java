package com.hikari.rent.station.repository;

import com.baomidou.mybatisplus.extension.repository.IRepository;
import com.hikari.rent.station.entity.Station;
import com.hikari.rent.station.repository.model.StationSearchRow;
import com.hikari.rent.station.repository.model.StationTreeRow;
import com.hikari.rent.station.vo.StationOptionVO;

import java.util.List;

public interface StationRepository extends IRepository<Station> {

  List<StationSearchRow> search(
    String keyword,
    String prefectureCode,
    int limit
  );

  List<StationOptionVO> findOperators();

  List<StationOptionVO> findLinesByOperatorCode(
    String operatorCode
  );

  List<StationOptionVO> findStationsByLineId(Long lineId);

  List<StationTreeRow> findTreeRows();
}
