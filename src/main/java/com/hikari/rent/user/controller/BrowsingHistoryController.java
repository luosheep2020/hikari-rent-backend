package com.hikari.rent.user.controller;

import java.util.List;

import com.hikari.rent.common.result.ApiResponse;
import com.hikari.rent.rental.vo.RentalListVO;
import com.hikari.rent.user.service.BrowsingHistoryService;

import jakarta.annotation.Resource;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user/history")
public class BrowsingHistoryController {

  @Resource
  private BrowsingHistoryService browsingHistoryService;

  @GetMapping
  public ApiResponse<List<RentalListVO>> getHistory() {

    return ApiResponse.success(
      browsingHistoryService.getHistory()
    );
  }

  @PostMapping("/{rentalId}")
  public ApiResponse<Void> recordView(
    @PathVariable("rentalId") Long rentalId
  ) {

    browsingHistoryService.recordView(rentalId);

    return ApiResponse.success();
  }

  @DeleteMapping("/{rentalId}")
  public ApiResponse<Void> remove(
    @PathVariable("rentalId") Long rentalId
  ) {

    browsingHistoryService.remove(rentalId);

    return ApiResponse.success();
  }

  @DeleteMapping
  public ApiResponse<Void> clear() {

    browsingHistoryService.clear();

    return ApiResponse.success();
  }
}
