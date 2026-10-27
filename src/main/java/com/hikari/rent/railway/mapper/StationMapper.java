package com.hikari.rent.railway.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hikari.rent.railway.entity.Station;
import com.hikari.rent.railway.repository.model.StationSearchRow;
import com.hikari.rent.railway.repository.model.StationTreeRow;
import com.hikari.rent.railway.vo.StationOptionVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StationMapper extends BaseMapper<Station> {

  List<StationSearchRow> search(
    @Param("keyword") String keyword,
    @Param("prefectureCode") String prefectureCode,
    @Param("limit") int limit
  );

  List<StationOptionVO> selectOperators();

  List<StationOptionVO> selectLinesByOperatorCode(
    @Param("operatorCode") String operatorCode
  );

  List<StationOptionVO> selectStationsByLineId(
    @Param("lineId") Long lineId
  );

  List<StationTreeRow> selectTreeRows();
}
