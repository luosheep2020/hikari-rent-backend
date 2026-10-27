package com.hikari.rent.dashboard.controller;

import com.hikari.rent.common.result.ApiResponse;
import com.hikari.rent.dashboard.service.DashboardService;
import com.hikari.rent.dashboard.vo.DashboardOverviewVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/dashboard")
public class DashboardController {

  @Resource
  private DashboardService dashboardService;

  @GetMapping("/overview")
  public ApiResponse<DashboardOverviewVO> getOverview() {
    return ApiResponse.success(
      dashboardService.getOverview()
    );
  }
}
