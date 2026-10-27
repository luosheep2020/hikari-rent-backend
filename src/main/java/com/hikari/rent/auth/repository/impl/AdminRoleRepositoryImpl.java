package com.hikari.rent.auth.repository.impl;

import com.hikari.rent.auth.mapper.AdminRoleMapper;
import com.hikari.rent.auth.repository.AdminRoleRepository;
import com.hikari.rent.auth.repository.model.AdminRoleRow;
import com.hikari.rent.auth.repository.model.AdminRoleUserRow;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AdminRoleRepositoryImpl
  implements AdminRoleRepository {

  @Resource
  private AdminRoleMapper adminRoleMapper;

  @Override
  public List<AdminRoleRow> findAllRoles() {
    return adminRoleMapper.selectAllRoles();
  }

  @Override
  public List<AdminRoleRow> findAllRolesForShare() {
    return adminRoleMapper.selectAllRolesForShare();
  }

  @Override
  public AdminRoleUserRow findUser(Long userId) {
    return adminRoleMapper.selectUser(userId);
  }

  @Override
  public AdminRoleUserRow findUserForUpdate(Long userId) {
    return adminRoleMapper.selectUserForUpdate(userId);
  }

  @Override
  public List<AdminRoleRow> findUserRoles(Long userId) {
    return adminRoleMapper.selectUserRoles(userId);
  }

  @Override
  public AdminRoleRow lockPlatformRole() {
    return adminRoleMapper.selectPlatformRoleForUpdate();
  }

  @Override
  public List<Long> findActivePlatformAdminIds(Long roleId) {
    return adminRoleMapper.selectActivePlatformAdminIds(roleId);
  }

  @Override
  public void deleteUserRoles(Long userId) {
    adminRoleMapper.deleteUserRoles(userId);
  }

  @Override
  public int insertUserRoles(
    Long userId,
    List<Long> roleIds
  ) {
    return adminRoleMapper.insertUserRoles(userId, roleIds);
  }

  @Override
  public void enqueueRentalSyncTasks(Long userId) {
    adminRoleMapper.enqueueRentalSyncTasks(userId);
  }
}
