package com.hikari.rent.dashboard.repository.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DashboardStatsRow {

  private Long userCount;

  private Long companyCount;

  private Long rentalCount;

  private Long pendingReviewCount;

  private Long publishedCount;

  private Long rentedCount;
}
