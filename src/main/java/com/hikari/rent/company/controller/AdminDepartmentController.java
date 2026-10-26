package com.hikari.rent.company.controller;

import com.hikari.rent.common.result.ApiResponse;
import com.hikari.rent.company.dto.AdminDepartmentSaveRequest;
import com.hikari.rent.company.dto.AdminDepartmentStatusRequest;
import com.hikari.rent.company.service.AdminDepartmentService;
import com.hikari.rent.company.vo.AdminDepartmentCreatedVO;
import com.hikari.rent.company.vo.AdminDepartmentTreeVO;
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
@RequestMapping("/api/admin/departments")
public class AdminDepartmentController {

  @Resource
  private AdminDepartmentService adminDepartmentService;

  @GetMapping("/tree")
  public ApiResponse<List<AdminDepartmentTreeVO>> getTree(
    @RequestParam(value = "companyId", required = false)
    Long companyId
  ) {
    return ApiResponse.success(
      adminDepartmentService.getTree(companyId)
    );
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ApiResponse<AdminDepartmentCreatedVO> create(
    @Valid @RequestBody AdminDepartmentSaveRequest request
  ) {
    return ApiResponse.success(
      adminDepartmentService.create(request)
    );
  }

  @PutMapping("/{id}")
  public ApiResponse<Void> update(
    @PathVariable("id") Long departmentId,
    @Valid @RequestBody AdminDepartmentSaveRequest request
  ) {
    adminDepartmentService.update(departmentId, request);

    return ApiResponse.success();
  }

  @PatchMapping("/status/{id}")
  public ApiResponse<Void> updateStatus(
    @PathVariable("id") Long departmentId,
    @Valid @RequestBody AdminDepartmentStatusRequest request
  ) {
    adminDepartmentService.updateStatus(departmentId, request);

    return ApiResponse.success();
  }
}
