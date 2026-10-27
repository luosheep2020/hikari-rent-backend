package com.hikari.rent.company.controller;

import com.hikari.rent.common.result.ApiResponse;
import com.hikari.rent.common.result.PageResult;
import com.hikari.rent.company.dto.AdminEmployeeCreateRequest;
import com.hikari.rent.company.dto.AdminEmployeePageRequest;
import com.hikari.rent.company.dto.AdminEmployeeStatusRequest;
import com.hikari.rent.company.dto.AdminEmployeeUpdateRequest;
import com.hikari.rent.company.service.AdminEmployeeService;
import com.hikari.rent.company.vo.AdminEmployeeCreatedVO;
import com.hikari.rent.company.vo.AdminEmployeeVO;
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
@RequestMapping("/api/admin/employees")
public class AdminEmployeeController {

  @Resource
  private AdminEmployeeService adminEmployeeService;

  @GetMapping
  public ApiResponse<PageResult<AdminEmployeeVO>> searchPage(
    @Valid @ModelAttribute AdminEmployeePageRequest request
  ) {
    return ApiResponse.success(
      adminEmployeeService.searchPage(request)
    );
  }

  @GetMapping("/{id}")
  public ApiResponse<AdminEmployeeVO> getDetail(
    @PathVariable("id") Long employeeId
  ) {
    return ApiResponse.success(
      adminEmployeeService.getDetail(employeeId)
    );
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ApiResponse<AdminEmployeeCreatedVO> create(
    @Valid @RequestBody AdminEmployeeCreateRequest request
  ) {
    return ApiResponse.success(
      adminEmployeeService.create(request)
    );
  }

  @PutMapping("/{id}")
  public ApiResponse<Void> update(
    @PathVariable("id") Long employeeId,
    @Valid @RequestBody AdminEmployeeUpdateRequest request
  ) {
    adminEmployeeService.update(employeeId, request);

    return ApiResponse.success();
  }

  @PatchMapping("/status/{id}")
  public ApiResponse<Void> updateStatus(
    @PathVariable("id") Long employeeId,
    @Valid @RequestBody AdminEmployeeStatusRequest request
  ) {
    adminEmployeeService.updateStatus(employeeId, request);

    return ApiResponse.success();
  }
}
