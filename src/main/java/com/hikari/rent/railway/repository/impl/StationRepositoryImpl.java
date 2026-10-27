package com.hikari.rent.railway.repository.impl;


import com.baomidou.mybatisplus.spring.repository.CrudRepository;
import com.hikari.rent.railway.entity.Station;
import com.hikari.rent.railway.mapper.StationMapper;
import com.hikari.rent.railway.repository.StationRepository;
import com.hikari.rent.railway.repository.model.StationSearchRow;
import com.hikari.rent.railway.repository.model.StationTreeRow;
import com.hikari.rent.railway.vo.StationOptionVO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class StationRepositoryImpl
  extends CrudRepository<StationMapper,Station>
  implements StationRepository{

  @Override
  public List<StationSearchRow> search(
    String keyword,
    String prefectureCode,
    int limit
  ) {
    return baseMapper.search(
      keyword,
      prefectureCode,
      limit
    );
  }

  @Override
  public List<StationOptionVO> findOperators() {
    return baseMapper.selectOperators();
  }

  @Override
  public List<StationOptionVO> findLinesByOperatorCode(String operatorCode) {
    return baseMapper.selectLinesByOperatorCode(
      operatorCode
    );
  }

  @Override
  public List<StationOptionVO> findStationsByLineId(Long lineId) {
    return baseMapper.selectStationsByLineId(lineId);
  }

  @Override
  public List<StationTreeRow> findTreeRows() {
    return baseMapper.selectTreeRows();
  }
}
