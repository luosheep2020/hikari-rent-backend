package com.hikari.rent.rental.controller;

import com.hikari.rent.common.result.ApiResponse;
import com.hikari.rent.rental.dto.admin.AdminRentalReviewRequest;
import com.hikari.rent.rental.service.AdminRentalReviewService;
import com.hikari.rent.rental.vo.admin.AdminRentalReviewedVO;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/rentals")
public class AdminRentalReviewController {

  @Resource
  private AdminRentalReviewService adminRentalReviewService;

  @PostMapping("/review/{id}")
  public ApiResponse<AdminRentalReviewedVO> review(
    @PathVariable("id") Long rentalId,
    @Valid @RequestBody AdminRentalReviewRequest request
  ) {
    return ApiResponse.success(
      adminRentalReviewService.review(rentalId, request)
    );
  }
}
