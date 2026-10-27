package com.hikari.rent.auth.service;

import com.hikari.rent.auth.dto.AdminRolePermissionsRequest;
import com.hikari.rent.auth.vo.AdminPermissionTreeVO;

import java.util.List;

public interface AdminPermissionService {

  List<AdminPermissionTreeVO> getTree();

  List<String> getRolePermissionIds(Long roleId);

  void updateRolePermissions(
    Long roleId,
    AdminRolePermissionsRequest request
  );
}
