package com.hikari.rent.dashboard.service.impl;

import com.hikari.rent.dashboard.repository.DashboardRepository;
import com.hikari.rent.dashboard.repository.model.DashboardStatsRow;
import com.hikari.rent.dashboard.service.DashboardService;
import com.hikari.rent.dashboard.vo.DashboardOverviewVO;
import com.hikari.rent.rental.model.RentalAccessScope;
import com.hikari.rent.rental.service.RentalAccessService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@PreAuthorize(
  "hasAnyRole('PLATFORM_ADMIN', 'COMPANY_ADMIN', 'AGENT')"
)
public class DashboardServiceImpl implements DashboardService {

  @Resource
  private RentalAccessService rentalAccessService;

  @Resource
  private DashboardRepository dashboardRepository;

  @Override
  @Transactional(readOnly = true)
  public DashboardOverviewVO getOverview() {
    RentalAccessScope scope =
      rentalAccessService.requireManagementScope();

    DashboardStatsRow rentalStats =
      dashboardRepository.findRentalStats(scope);

    Long userCount = null;
    Long companyCount = null;
    Long employeeCount = null;

    if (scope.isPlatform()) {
      DashboardStatsRow platformStats =
        dashboardRepository.findPlatformStats();

      userCount = platformStats.getUserCount();
      companyCount = platformStats.getCompanyCount();

    } else if (scope.isCompany()) {
      employeeCount =
        dashboardRepository.countCompanyEmployees(
          scope.companyId()
        );
    }

    return new DashboardOverviewVO(
      scope.type().name(),
      userCount,
      companyCount,
      employeeCount,
      rentalStats.getRentalCount(),
      rentalStats.getPendingReviewCount(),
      rentalStats.getPublishedCount(),
      rentalStats.getRentedCount()
    );
  }
}
