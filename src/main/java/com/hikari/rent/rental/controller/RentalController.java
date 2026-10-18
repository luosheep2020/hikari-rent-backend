package com.hikari.rent.rental.controller;

import com.hikari.rent.common.result.ApiResponse;
import com.hikari.rent.common.result.PageResult;
import com.hikari.rent.rental.dto.RentalMapRequest;
import com.hikari.rent.rental.dto.RentalPageRequest;
import com.hikari.rent.rental.service.RentalService;
import com.hikari.rent.rental.vo.RentalDetailVO;
import com.hikari.rent.rental.vo.RentalFeatureOptionVO;
import com.hikari.rent.rental.vo.RentalListVO;
import com.hikari.rent.rental.vo.RentalMapVO;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rentals")
public class RentalController {

  @Resource
  private RentalService rentalService;

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

  @GetMapping("/features")
  public ApiResponse<List<RentalFeatureOptionVO>> getFeatureOptions() {
    return ApiResponse.success(
      rentalService.getFeatureOptions()
    );
  }

  @GetMapping("/map")
  public ApiResponse<RentalMapVO> searchMap(
    @Valid @ModelAttribute RentalMapRequest request
  ) {
    return ApiResponse.success(
      rentalService.searchMap(request)
    );
  }
}
