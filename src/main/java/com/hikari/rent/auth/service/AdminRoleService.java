package com.hikari.rent.auth.service;

import com.hikari.rent.auth.dto.AdminUserRolesRequest;
import com.hikari.rent.auth.vo.AdminRoleVO;

import java.util.List;

public interface AdminRoleService {

  List<AdminRoleVO> getRoles();

  List<AdminRoleVO> getUserRoles(Long userId);

  void updateUserRoles(
    Long userId,
    AdminUserRolesRequest request
  );
}
