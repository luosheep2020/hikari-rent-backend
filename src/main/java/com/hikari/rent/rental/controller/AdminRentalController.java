package com.hikari.rent.rental.controller;

import com.hikari.rent.common.result.ApiResponse;
import com.hikari.rent.common.result.PageResult;
import com.hikari.rent.rental.dto.admin.AdminRentalCreateRequest;
import com.hikari.rent.rental.dto.admin.AdminRentalPageRequest;
import com.hikari.rent.rental.dto.admin.AdminRentalSubmitReviewRequest;
import com.hikari.rent.rental.dto.admin.AdminRentalUpdateRequest;
import com.hikari.rent.rental.service.AdminRentalService;
import com.hikari.rent.rental.vo.admin.*;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/rentals")
public class AdminRentalController {

  @Resource
  private AdminRentalService adminRentalService;

  @GetMapping
  public ApiResponse<PageResult<AdminRentalListVO>> searchPage(
    @Valid @ModelAttribute AdminRentalPageRequest request
  ) {
    return ApiResponse.success(
      adminRentalService.searchPage(request)
    );
  }

  @GetMapping("/{id}")
  public ApiResponse<AdminRentalDetailVO> getDetail(
    @PathVariable("id") Long id
  ) {
    return ApiResponse.success(
      adminRentalService.getDetail(id)
    );
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ApiResponse<AdminRentalCreatedVO> createDraft(
    @Valid @RequestBody AdminRentalCreateRequest request
  ) {
    return ApiResponse.success(
      adminRentalService.createDraft(request)
    );
  }

  @PutMapping("/{id}")
  public ApiResponse<AdminRentalUpdatedVO> updateDraft(
    @PathVariable("id") Long id,
    @Valid @RequestBody AdminRentalUpdateRequest request
  ) {
    return ApiResponse.success(
      adminRentalService.updateDraft(
        id,
        request
      )
    );
  }

  @PostMapping("/{id}/submit-review")
  public ApiResponse<AdminRentalReviewSubmittedVO> submitReview(
    @PathVariable("id") Long rentalId,
    @Valid @RequestBody AdminRentalSubmitReviewRequest request
  ) {
    return ApiResponse.success(
      adminRentalService.submitReview(
        rentalId,
        request
      )
    );
  }
}
