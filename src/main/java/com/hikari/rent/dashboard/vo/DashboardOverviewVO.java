package com.hikari.rent.dashboard.vo;

public record DashboardOverviewVO(
  String scope,
  Long userCount,
  Long companyCount,
  Long employeeCount,
  long rentalCount,
  long pendingReviewCount,
  long publishedCount,
  long rentedCount
) {
}
