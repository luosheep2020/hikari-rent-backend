package com.hikari.rent.rental.controller;

import com.hikari.rent.common.result.ApiResponse;
import com.hikari.rent.rental.dto.admin.AdminRentalPublicationRequest;
import com.hikari.rent.rental.service.AdminRentalPublicationService;
import com.hikari.rent.rental.vo.admin.AdminRentalPublicationVO;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/rentals")
public class AdminRentalPublicationController {

  @Resource
  private AdminRentalPublicationService adminRentalPublicationService;

  @PostMapping("/publish/{id}")
  public ApiResponse<AdminRentalPublicationVO> publish(
    @PathVariable("id") Long rentalId,
    @Valid @RequestBody AdminRentalPublicationRequest request
  ) {
    return ApiResponse.success(
      adminRentalPublicationService.publish(
        rentalId,
        request
      )
    );
  }

  @PostMapping("/unpublish/{id}")
  public ApiResponse<AdminRentalPublicationVO> unpublish(
    @PathVariable("id") Long rentalId,
    @Valid @RequestBody AdminRentalPublicationRequest request
  ) {
    return ApiResponse.success(
      adminRentalPublicationService.unpublish(
        rentalId,
        request
      )
    );
  }

  @PostMapping("/rented/{id}")
  public ApiResponse<AdminRentalPublicationVO> markRented(
    @PathVariable("id") Long rentalId,
    @Valid @RequestBody AdminRentalPublicationRequest request
  ) {
    return ApiResponse.success(
      adminRentalPublicationService.markRented(
        rentalId,
        request
      )
    );
  }
}
