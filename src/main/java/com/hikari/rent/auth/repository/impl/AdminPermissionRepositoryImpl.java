package com.hikari.rent.auth.repository.impl;

import com.hikari.rent.auth.mapper.AdminPermissionMapper;
import com.hikari.rent.auth.repository.AdminPermissionRepository;
import com.hikari.rent.auth.repository.model.AdminPermissionRow;
import com.hikari.rent.auth.repository.model.AdminRoleRow;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AdminPermissionRepositoryImpl
  implements AdminPermissionRepository {

  @Resource
  private AdminPermissionMapper adminPermissionMapper;

  @Override
  public List<AdminPermissionRow> findAllPermissions() {
    return adminPermissionMapper.selectAllPermissions();
  }

  @Override
  public List<AdminPermissionRow> findAllPermissionsForShare() {
    return adminPermissionMapper.selectAllPermissionsForShare();
  }

  @Override
  public AdminRoleRow findRoleById(Long roleId) {
    return adminPermissionMapper.selectRoleById(roleId);
  }

  @Override
  public AdminRoleRow findRoleByIdForUpdate(Long roleId) {
    return adminPermissionMapper.selectRoleByIdForUpdate(roleId);
  }

  @Override
  public List<Long> findRolePermissionIds(Long roleId) {
    return adminPermissionMapper.selectRolePermissionIds(roleId);
  }

  @Override
  public void deleteRolePermissions(Long roleId) {
    adminPermissionMapper.deleteRolePermissions(roleId);
  }

  @Override
  public int insertRolePermissions(
    Long roleId,
    List<Long> permissionIds
  ) {
    if (permissionIds == null || permissionIds.isEmpty()) {
      return 0;
    }

    return adminPermissionMapper.insertRolePermissions(
      roleId,
      permissionIds
    );
  }
}
