package com.hikari.rent.company.controller;

import com.hikari.rent.common.result.ApiResponse;
import com.hikari.rent.company.dto.AdminPositionSaveRequest;
import com.hikari.rent.company.dto.AdminPositionStatusRequest;
import com.hikari.rent.company.service.AdminPositionService;
import com.hikari.rent.company.vo.AdminPositionCreatedVO;
import com.hikari.rent.company.vo.AdminPositionVO;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/positions")
public class AdminPositionController {

  @Resource
  private AdminPositionService adminPositionService;

  @GetMapping
  public ApiResponse<List<AdminPositionVO>> getList(
    @RequestParam(value = "companyId", required = false)
    Long companyId
  ) {
    return ApiResponse.success(
      adminPositionService.getList(companyId)
    );
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ApiResponse<AdminPositionCreatedVO> create(
    @Valid @RequestBody AdminPositionSaveRequest request
  ) {
    return ApiResponse.success(
      adminPositionService.create(request)
    );
  }

  @PutMapping("/{id}")
  public ApiResponse<Void> update(
    @PathVariable("id") Long positionId,
    @Valid @RequestBody AdminPositionSaveRequest request
  ) {
    adminPositionService.update(positionId, request);

    return ApiResponse.success();
  }

  @PatchMapping("/status/{id}")
  public ApiResponse<Void> updateStatus(
    @PathVariable("id") Long positionId,
    @Valid @RequestBody AdminPositionStatusRequest request
  ) {
    adminPositionService.updateStatus(positionId, request);

    return ApiResponse.success();
  }
}
