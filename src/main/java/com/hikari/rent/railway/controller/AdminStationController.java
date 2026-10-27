package com.hikari.rent.railway.controller;

import com.hikari.rent.common.result.ApiResponse;
import com.hikari.rent.common.result.PageResult;
import com.hikari.rent.railway.dto.AdminStationCreateRequest;
import com.hikari.rent.railway.dto.AdminStationPageRequest;
import com.hikari.rent.railway.dto.AdminStationStatusRequest;
import com.hikari.rent.railway.dto.AdminStationUpdateRequest;
import com.hikari.rent.railway.service.AdminStationService;
import com.hikari.rent.railway.vo.AdminStationCreatedVO;
import com.hikari.rent.railway.vo.AdminStationVO;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/stations")
public class AdminStationController {

  @Resource
  private AdminStationService adminStationService;

  @GetMapping
  public ApiResponse<PageResult<AdminStationVO>> searchPage(
    @Valid @ModelAttribute AdminStationPageRequest request
  ) {
    return ApiResponse.success(
      adminStationService.searchPage(request)
    );
  }

  @GetMapping("/{id}")
  public ApiResponse<AdminStationVO> getDetail(
    @PathVariable("id") Long stationId
  ) {
    return ApiResponse.success(
      adminStationService.getDetail(stationId)
    );
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ApiResponse<AdminStationCreatedVO> create(
    @Valid @RequestBody AdminStationCreateRequest request
  ) {
    return ApiResponse.success(
      adminStationService.create(request)
    );
  }

  @PutMapping("/{id}")
  public ApiResponse<Void> update(
    @PathVariable("id") Long stationId,
    @Valid @RequestBody AdminStationUpdateRequest request
  ) {
    adminStationService.update(stationId, request);

    return ApiResponse.success();
  }

  @PatchMapping("/status/{id}")
  public ApiResponse<Void> updateStatus(
    @PathVariable("id") Long stationId,
    @Valid @RequestBody AdminStationStatusRequest request
  ) {
    adminStationService.updateStatus(stationId, request);

    return ApiResponse.success();
  }
}
