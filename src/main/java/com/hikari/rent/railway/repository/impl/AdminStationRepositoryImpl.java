package com.hikari.rent.railway.repository.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hikari.rent.railway.dto.AdminStationPageRequest;
import com.hikari.rent.railway.entity.Station;
import com.hikari.rent.railway.mapper.AdminStationMapper;
import com.hikari.rent.railway.repository.AdminStationRepository;
import com.hikari.rent.railway.repository.model.AdminStationRow;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Repository;

@Repository
public class AdminStationRepositoryImpl
  implements AdminStationRepository {

  @Resource
  private AdminStationMapper adminStationMapper;

  @Override
  public IPage<AdminStationRow> searchPage(
    AdminStationPageRequest request
  ) {
    Page<AdminStationRow> page = new Page<>(
      request.getPage(),
      request.getPageSize()
    );

    page.setOptimizeCountSql(false);

    return adminStationMapper.searchPage(page, request);
  }

  @Override
  public AdminStationRow findDetail(Long stationId) {
    return adminStationMapper.selectDetail(stationId);
  }

  @Override
  public Station findById(Long stationId) {
    return adminStationMapper.selectById(stationId);
  }

  @Override
  public Station findByIdForUpdate(
    Long stationId,
    Long lineId
  ) {
    return adminStationMapper.selectByIdForUpdate(
      stationId,
      lineId
    );
  }

  @Override
  public void insert(Station station) {
    int inserted = adminStationMapper.insert(station);

    if (inserted != 1 || station.getId() == null) {
      throw new IllegalStateException(
        "Station insert did not return a generated ID"
      );
    }
  }

  @Override
  public void update(Station station) {
    adminStationMapper.update(station);
  }

  @Override
  public void updateStatus(
    Long stationId,
    Long lineId,
    int status
  ) {
    adminStationMapper.updateStatus(
      stationId,
      lineId,
      status
    );
  }

  @Override
  public void enqueueRentalSyncTasks(Long stationId) {
    adminStationMapper.enqueueRentalSyncTasks(stationId);
  }
}
