package com.hikari.rent.railway.repository.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hikari.rent.railway.dto.AdminRailwayLinePageRequest;
import com.hikari.rent.railway.entity.RailwayLine;
import com.hikari.rent.railway.mapper.AdminRailwayLineMapper;
import com.hikari.rent.railway.repository.AdminRailwayLineRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Repository;

@Repository
public class AdminRailwayLineRepositoryImpl
  implements AdminRailwayLineRepository {

  @Resource
  private AdminRailwayLineMapper adminRailwayLineMapper;

  @Override
  public IPage<RailwayLine> searchPage(
    AdminRailwayLinePageRequest request
  ) {
    Page<RailwayLine> page = new Page<>(
      request.getPage(),
      request.getPageSize()
    );

    page.setOptimizeCountSql(false);

    return adminRailwayLineMapper.searchPage(page, request);
  }

  @Override
  public RailwayLine findById(Long lineId) {
    return adminRailwayLineMapper.selectById(lineId);
  }

  @Override
  public RailwayLine findByIdForUpdate(Long lineId) {
    return adminRailwayLineMapper.selectByIdForUpdate(lineId);
  }

  @Override
  public String findOperatorName(String operatorSourceCode) {
    return adminRailwayLineMapper.selectOperatorName(
      operatorSourceCode
    );
  }

  @Override
  public void insert(RailwayLine line) {
    int inserted = adminRailwayLineMapper.insert(line);

    if (inserted != 1 || line.getId() == null) {
      throw new IllegalStateException(
        "Railway line insert did not return a generated ID"
      );
    }
  }

  @Override
  public void update(RailwayLine line) {
    adminRailwayLineMapper.update(line);
  }

  @Override
  public void updateStatus(Long lineId, int status) {
    adminRailwayLineMapper.updateStatus(lineId, status);
  }

  @Override
  public void updateStationLineNames(Long lineId, String name) {
    adminRailwayLineMapper.updateStationLineNames(lineId, name);
  }

  @Override
  public void enqueueRentalSyncTasks(Long lineId) {
    adminRailwayLineMapper.enqueueRentalSyncTasks(lineId);
  }
}
