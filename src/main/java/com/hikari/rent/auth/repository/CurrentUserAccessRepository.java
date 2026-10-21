package com.hikari.rent.auth.repository;

import com.hikari.rent.auth.repository.model.CurrentUserRoleRow;

import java.util.List;

public interface CurrentUserAccessRepository {

  List<CurrentUserRoleRow> findUserRoles(Long userId);
}
