package com.hikari.rent.auth.repository;

import com.hikari.rent.auth.repository.model.AdminPermissionRow;
import com.hikari.rent.auth.repository.model.AdminRoleRow;

import java.util.List;

public interface AdminPermissionRepository {

  List<AdminPermissionRow> findAllPermissions();

  List<AdminPermissionRow> findAllPermissionsForShare();

  AdminRoleRow findRoleById(Long roleId);

  AdminRoleRow findRoleByIdForUpdate(Long roleId);

  List<Long> findRolePermissionIds(Long roleId);

  void deleteRolePermissions(Long roleId);

  int insertRolePermissions(
    Long roleId,
    List<Long> permissionIds
  );
}
