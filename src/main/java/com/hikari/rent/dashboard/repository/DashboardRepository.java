package com.hikari.rent.dashboard.repository;

import com.hikari.rent.dashboard.repository.model.DashboardStatsRow;
import com.hikari.rent.rental.model.RentalAccessScope;

public interface DashboardRepository {

  DashboardStatsRow findRentalStats(
    RentalAccessScope scope
  );

  DashboardStatsRow findPlatformStats();

  long countCompanyEmployees(Long companyId);
}
