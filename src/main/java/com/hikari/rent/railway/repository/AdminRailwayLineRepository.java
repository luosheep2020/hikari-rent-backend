package com.hikari.rent.railway.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hikari.rent.railway.dto.AdminRailwayLinePageRequest;
import com.hikari.rent.railway.entity.RailwayLine;

public interface AdminRailwayLineRepository {

  IPage<RailwayLine> searchPage(
    AdminRailwayLinePageRequest request
  );

  RailwayLine findById(Long lineId);

  RailwayLine findByIdForUpdate(Long lineId);

  String findOperatorName(String operatorSourceCode);

  void insert(RailwayLine line);

  void update(RailwayLine line);

  void updateStatus(Long lineId, int status);

  void updateStationLineNames(Long lineId, String name);

  void enqueueRentalSyncTasks(Long lineId);
}
