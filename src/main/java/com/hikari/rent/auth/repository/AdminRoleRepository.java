package com.hikari.rent.auth.repository;

import com.hikari.rent.auth.repository.model.AdminRoleRow;
import com.hikari.rent.auth.repository.model.AdminRoleUserRow;

import java.util.List;

public interface AdminRoleRepository {

  List<AdminRoleRow> findAllRoles();

  List<AdminRoleRow> findAllRolesForShare();

  AdminRoleUserRow findUser(Long userId);

  AdminRoleUserRow findUserForUpdate(Long userId);

  List<AdminRoleRow> findUserRoles(Long userId);

  AdminRoleRow lockPlatformRole();

  List<Long> findActivePlatformAdminIds(Long roleId);

  void deleteUserRoles(Long userId);

  int insertUserRoles(Long userId, List<Long> roleIds);

  void enqueueRentalSyncTasks(Long userId);
}
