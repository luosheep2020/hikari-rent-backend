package com.hikari.rent.auth.repository.impl;

import com.hikari.rent.auth.mapper.AuthPermissionMapper;
import com.hikari.rent.auth.repository.AuthPermissionRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AuthPermissionRepositoryImpl
  implements AuthPermissionRepository {

  @Resource
  private AuthPermissionMapper authPermissionMapper;

  @Override
  public List<String> findPermissionCodes(Long userId) {
    return authPermissionMapper.selectPermissionCodes(userId);
  }

  @Override
  public List<String> findAllPermissionCodes() {
    return authPermissionMapper.selectAllPermissionCodes();
  }
}
