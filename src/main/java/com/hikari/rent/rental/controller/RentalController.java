package com.hikari.rent.rental.controller;

import com.hikari.rent.common.result.ApiResponse;
import com.hikari.rent.common.result.PageResult;
import com.hikari.rent.rental.dto.RentalPageRequest;
import com.hikari.rent.rental.service.RentalService;
import com.hikari.rent.rental.vo.RentalDetailVO;
import com.hikari.rent.rental.vo.RentalListVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rentals")
@RequiredArgsConstructor
public class RentalController {

  private final RentalService rentalService;

  @GetMapping
  public ApiResponse<PageResult<RentalListVO>> searchPage(
    @Valid @ModelAttribute RentalPageRequest request
  ) {
    return ApiResponse.success(
      rentalService.searchPage(request)
    );
  }

  @GetMapping("/{id}")
  public ApiResponse<RentalDetailVO> getDetail(
    @PathVariable("id") Long id
  ) {
    return ApiResponse.success(
      rentalService.getDetail(id)
    );
  }
}
