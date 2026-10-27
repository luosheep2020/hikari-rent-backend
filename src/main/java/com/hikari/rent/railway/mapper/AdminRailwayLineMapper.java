package com.hikari.rent.railway.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hikari.rent.railway.dto.AdminRailwayLinePageRequest;
import com.hikari.rent.railway.entity.RailwayLine;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AdminRailwayLineMapper {

  IPage<RailwayLine> searchPage(
    IPage<RailwayLine> page,
    @Param("query") AdminRailwayLinePageRequest query
  );

  RailwayLine selectById(
    @Param("lineId") Long lineId
  );

  RailwayLine selectByIdForUpdate(
    @Param("lineId") Long lineId
  );

  String selectOperatorName(
    @Param("operatorSourceCode") String operatorSourceCode
  );

  int insert(
    @Param("line") RailwayLine line
  );

  int update(
    @Param("line") RailwayLine line
  );

  int updateStatus(
    @Param("lineId") Long lineId,
    @Param("status") int status
  );

  int updateStationLineNames(
    @Param("lineId") Long lineId,
    @Param("name") String name
  );

  int enqueueRentalSyncTasks(
    @Param("lineId") Long lineId
  );
}
