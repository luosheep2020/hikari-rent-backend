package com.hikari.rent.auth.mapper;

import com.hikari.rent.auth.repository.model.AdminPermissionRow;
import com.hikari.rent.auth.repository.model.AdminRoleRow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminPermissionMapper {

  List<AdminPermissionRow> selectAllPermissions();

  List<AdminPermissionRow> selectAllPermissionsForShare();

  AdminRoleRow selectRoleById(
    @Param("roleId") Long roleId
  );

  AdminRoleRow selectRoleByIdForUpdate(
    @Param("roleId") Long roleId
  );

  List<Long> selectRolePermissionIds(
    @Param("roleId") Long roleId
  );

  int deleteRolePermissions(
    @Param("roleId") Long roleId
  );

  int insertRolePermissions(
    @Param("roleId") Long roleId,
    @Param("permissionIds") List<Long> permissionIds
  );
}
