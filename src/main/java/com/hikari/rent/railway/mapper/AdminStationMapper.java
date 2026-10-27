package com.hikari.rent.railway.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hikari.rent.railway.dto.AdminStationPageRequest;
import com.hikari.rent.railway.entity.Station;
import com.hikari.rent.railway.repository.model.AdminStationRow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AdminStationMapper {

  IPage<AdminStationRow> searchPage(
    IPage<AdminStationRow> page,
    @Param("query") AdminStationPageRequest query
  );

  AdminStationRow selectDetail(
    @Param("stationId") Long stationId
  );

  Station selectById(
    @Param("stationId") Long stationId
  );

  Station selectByIdForUpdate(
    @Param("stationId") Long stationId,
    @Param("lineId") Long lineId
  );

  int insert(
    @Param("station") Station station
  );

  int update(
    @Param("station") Station station
  );

  int updateStatus(
    @Param("stationId") Long stationId,
    @Param("lineId") Long lineId,
    @Param("status") int status
  );

  int enqueueRentalSyncTasks(
    @Param("stationId") Long stationId
  );
}
