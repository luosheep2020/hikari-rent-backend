package com.hikari.rent.auth.controller;

import com.hikari.rent.auth.dto.AdminUserRolesRequest;
import com.hikari.rent.auth.service.AdminRoleService;
import com.hikari.rent.auth.vo.AdminRoleVO;
import com.hikari.rent.common.result.ApiResponse;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminRoleController {

  @Resource
  private AdminRoleService adminRoleService;

  @GetMapping("/roles")
  public ApiResponse<List<AdminRoleVO>> getRoles() {
    return ApiResponse.success(
      adminRoleService.getRoles()
    );
  }

  @GetMapping("/users/roles/{id}")
  public ApiResponse<List<AdminRoleVO>> getUserRoles(
    @PathVariable("id") Long userId
  ) {
    return ApiResponse.success(
      adminRoleService.getUserRoles(userId)
    );
  }

  @PutMapping("/users/roles/{id}")
  public ApiResponse<Void> updateUserRoles(
    @PathVariable("id") Long userId,
    @Valid @RequestBody AdminUserRolesRequest request
  ) {
    adminRoleService.updateUserRoles(userId, request);

    return ApiResponse.success();
  }
}
