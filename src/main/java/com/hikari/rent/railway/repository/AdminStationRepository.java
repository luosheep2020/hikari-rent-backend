package com.hikari.rent.railway.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hikari.rent.railway.dto.AdminStationPageRequest;
import com.hikari.rent.railway.entity.Station;
import com.hikari.rent.railway.repository.model.AdminStationRow;

public interface AdminStationRepository {

  IPage<AdminStationRow> searchPage(
    AdminStationPageRequest request
  );

  AdminStationRow findDetail(Long stationId);

  Station findById(Long stationId);

  Station findByIdForUpdate(
    Long stationId,
    Long lineId
  );

  void insert(Station station);

  void update(Station station);

  void updateStatus(
    Long stationId,
    Long lineId,
    int status
  );

  void enqueueRentalSyncTasks(Long stationId);
}
