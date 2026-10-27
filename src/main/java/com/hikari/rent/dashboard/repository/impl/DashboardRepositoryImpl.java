package com.hikari.rent.dashboard.repository.impl;

import com.hikari.rent.dashboard.mapper.DashboardMapper;
import com.hikari.rent.dashboard.repository.DashboardRepository;
import com.hikari.rent.dashboard.repository.model.DashboardStatsRow;
import com.hikari.rent.rental.model.RentalAccessScope;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Repository;

@Repository
public class DashboardRepositoryImpl
  implements DashboardRepository {

  @Resource
  private DashboardMapper dashboardMapper;

  @Override
  public DashboardStatsRow findRentalStats(
    RentalAccessScope scope
  ) {
    return dashboardMapper.selectRentalStats(scope);
  }

  @Override
  public DashboardStatsRow findPlatformStats() {
    return dashboardMapper.selectPlatformStats();
  }

  @Override
  public long countCompanyEmployees(Long companyId) {
    return dashboardMapper.countCompanyEmployees(companyId);
  }
}
