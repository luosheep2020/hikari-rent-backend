package com.hikari.rent.auth.mapper;

import com.hikari.rent.auth.repository.model.AdminRoleRow;
import com.hikari.rent.auth.repository.model.AdminRoleUserRow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminRoleMapper {

  List<AdminRoleRow> selectAllRoles();

  List<AdminRoleRow> selectAllRolesForShare();

  AdminRoleUserRow selectUser(
    @Param("userId") Long userId
  );

  AdminRoleUserRow selectUserForUpdate(
    @Param("userId") Long userId
  );

  List<AdminRoleRow> selectUserRoles(
    @Param("userId") Long userId
  );

  AdminRoleRow selectPlatformRoleForUpdate();

  List<Long> selectActivePlatformAdminIds(
    @Param("roleId") Long roleId
  );

  int deleteUserRoles(
    @Param("userId") Long userId
  );

  int insertUserRoles(
    @Param("userId") Long userId,
    @Param("roleIds") List<Long> roleIds
  );

  int enqueueRentalSyncTasks(
    @Param("userId") Long userId
  );
}
