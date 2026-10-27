package com.hikari.rent.dashboard.mapper;

import com.hikari.rent.dashboard.repository.model.DashboardStatsRow;
import com.hikari.rent.rental.model.RentalAccessScope;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface DashboardMapper {

  DashboardStatsRow selectRentalStats(
    @Param("scope") RentalAccessScope scope
  );

  DashboardStatsRow selectPlatformStats();

  long countCompanyEmployees(
    @Param("companyId") Long companyId
  );
}
