package com.hikari.rent.user.controller;

import com.hikari.rent.common.result.ApiResponse;
import com.hikari.rent.common.result.PageResult;
import com.hikari.rent.user.dto.AdminUserPageRequest;
import com.hikari.rent.user.dto.AdminUserStatusRequest;
import com.hikari.rent.user.service.AdminUserService;
import com.hikari.rent.user.vo.AdminUserVO;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

  @Resource
  private AdminUserService adminUserService;

  @GetMapping
  public ApiResponse<PageResult<AdminUserVO>> searchPage(
    @Valid @ModelAttribute AdminUserPageRequest request
  ) {
    return ApiResponse.success(
      adminUserService.searchPage(request)
    );
  }

  @GetMapping("/{id}")
  public ApiResponse<AdminUserVO> getDetail(
    @PathVariable("id") Long userId
  ) {
    return ApiResponse.success(
      adminUserService.getDetail(userId)
    );
  }

  @PatchMapping("/status/{id}")
  public ApiResponse<Void> updateStatus(
    @PathVariable("id") Long userId,
    @Valid @RequestBody AdminUserStatusRequest request
  ) {
    adminUserService.updateStatus(userId, request);

    return ApiResponse.success();
  }
}
