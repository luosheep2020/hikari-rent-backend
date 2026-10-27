package com.hikari.rent.auth.repository;

import java.util.List;

public interface AuthPermissionRepository {

  List<String> findPermissionCodes(Long userId);

  List<String> findAllPermissionCodes();
}
