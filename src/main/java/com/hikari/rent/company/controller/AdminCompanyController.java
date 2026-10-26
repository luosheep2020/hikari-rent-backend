package com.hikari.rent.company.controller;

import com.hikari.rent.common.result.ApiResponse;
import com.hikari.rent.common.result.PageResult;
import com.hikari.rent.company.dto.AdminCompanyPageRequest;
import com.hikari.rent.company.dto.AdminCompanySaveRequest;
import com.hikari.rent.company.dto.AdminCompanyStatusRequest;
import com.hikari.rent.company.service.AdminCompanyService;
import com.hikari.rent.company.vo.AdminCompanyCreatedVO;
import com.hikari.rent.company.vo.AdminCompanyVO;
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
@RequestMapping("/api/admin/companies")
public class AdminCompanyController {

  @Resource
  private AdminCompanyService adminCompanyService;

  @GetMapping
  public ApiResponse<PageResult<AdminCompanyVO>> searchPage(
    @Valid @ModelAttribute AdminCompanyPageRequest request
  ) {
    return ApiResponse.success(
      adminCompanyService.searchPage(request)
    );
  }

  @GetMapping("/{id}")
  public ApiResponse<AdminCompanyVO> getDetail(
    @PathVariable("id") Long companyId
  ) {
    return ApiResponse.success(
      adminCompanyService.getDetail(companyId)
    );
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ApiResponse<AdminCompanyCreatedVO> create(
    @Valid @RequestBody AdminCompanySaveRequest request
  ) {
    return ApiResponse.success(
      adminCompanyService.create(request)
    );
  }

  @PutMapping("/{id}")
  public ApiResponse<Void> update(
    @PathVariable("id") Long companyId,
    @Valid @RequestBody AdminCompanySaveRequest request
  ) {
    adminCompanyService.update(companyId, request);

    return ApiResponse.success();
  }

  @PatchMapping("/status/{id}")
  public ApiResponse<Void> updateStatus(
    @PathVariable("id") Long companyId,
    @Valid @RequestBody AdminCompanyStatusRequest request
  ) {
    adminCompanyService.updateStatus(companyId, request);

    return ApiResponse.success();
  }
}
