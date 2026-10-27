package com.hikari.rent.railway.controller;

import com.hikari.rent.common.result.ApiResponse;
import com.hikari.rent.common.result.PageResult;
import com.hikari.rent.railway.dto.AdminRailwayLineCreateRequest;
import com.hikari.rent.railway.dto.AdminRailwayLinePageRequest;
import com.hikari.rent.railway.dto.AdminRailwayLineStatusRequest;
import com.hikari.rent.railway.dto.AdminRailwayLineUpdateRequest;
import com.hikari.rent.railway.service.AdminRailwayLineService;
import com.hikari.rent.railway.vo.AdminRailwayLineCreatedVO;
import com.hikari.rent.railway.vo.AdminRailwayLineVO;
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
@RequestMapping("/api/admin/railway-lines")
public class AdminRailwayLineController {

  @Resource
  private AdminRailwayLineService adminRailwayLineService;

  @GetMapping
  public ApiResponse<PageResult<AdminRailwayLineVO>> searchPage(
    @Valid @ModelAttribute AdminRailwayLinePageRequest request
  ) {
    return ApiResponse.success(
      adminRailwayLineService.searchPage(request)
    );
  }

  @GetMapping("/{id}")
  public ApiResponse<AdminRailwayLineVO> getDetail(
    @PathVariable("id") Long lineId
  ) {
    return ApiResponse.success(
      adminRailwayLineService.getDetail(lineId)
    );
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ApiResponse<AdminRailwayLineCreatedVO> create(
    @Valid @RequestBody AdminRailwayLineCreateRequest request
  ) {
    return ApiResponse.success(
      adminRailwayLineService.create(request)
    );
  }

  @PutMapping("/{id}")
  public ApiResponse<Void> update(
    @PathVariable("id") Long lineId,
    @Valid @RequestBody AdminRailwayLineUpdateRequest request
  ) {
    adminRailwayLineService.update(lineId, request);

    return ApiResponse.success();
  }

  @PatchMapping("/status/{id}")
  public ApiResponse<Void> updateStatus(
    @PathVariable("id") Long lineId,
    @Valid @RequestBody AdminRailwayLineStatusRequest request
  ) {
    adminRailwayLineService.updateStatus(lineId, request);

    return ApiResponse.success();
  }
}
