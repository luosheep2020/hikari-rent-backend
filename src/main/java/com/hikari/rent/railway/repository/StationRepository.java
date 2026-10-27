package com.hikari.rent.railway.repository;

import com.baomidou.mybatisplus.extension.repository.IRepository;
import com.hikari.rent.railway.entity.Station;
import com.hikari.rent.railway.repository.model.StationSearchRow;
import com.hikari.rent.railway.repository.model.StationTreeRow;
import com.hikari.rent.railway.vo.StationOptionVO;

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
