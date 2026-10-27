package com.hikari.rent.auth.controller;

import com.hikari.rent.auth.dto.AdminRolePermissionsRequest;
import com.hikari.rent.auth.service.AdminPermissionService;
import com.hikari.rent.auth.vo.AdminPermissionTreeVO;
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
public class AdminPermissionController {

  @Resource
  private AdminPermissionService adminPermissionService;

  @GetMapping("/permissions/tree")
  public ApiResponse<List<AdminPermissionTreeVO>> getTree() {
    return ApiResponse.success(
      adminPermissionService.getTree()
    );
  }

  @GetMapping("/roles/permissions/{id}")
  public ApiResponse<List<String>> getRolePermissionIds(
    @PathVariable("id") Long roleId
  ) {
    return ApiResponse.success(
      adminPermissionService.getRolePermissionIds(roleId)
    );
  }

  @PutMapping("/roles/permissions/{id}")
  public ApiResponse<Void> updateRolePermissions(
    @PathVariable("id") Long roleId,
    @Valid @RequestBody AdminRolePermissionsRequest request
  ) {
    adminPermissionService.updateRolePermissions(
      roleId,
      request
    );

    return ApiResponse.success();
  }
}
