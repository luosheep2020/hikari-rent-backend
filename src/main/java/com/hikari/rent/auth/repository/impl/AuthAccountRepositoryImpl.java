package com.hikari.rent.auth.repository.impl;

import com.hikari.rent.auth.mapper.AuthAccountMapper;
import com.hikari.rent.auth.repository.AuthAccountRepository;
import com.hikari.rent.auth.repository.model.AuthAccountRow;
import com.hikari.rent.auth.repository.model.AuthRegisterRow;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AuthAccountRepositoryImpl
  implements AuthAccountRepository {

  @Resource
  private AuthAccountMapper authAccountMapper;

  @Override
  public AuthAccountRow findByEmail(String email) {
    return authAccountMapper.selectByEmail(email);
  }

  @Override
  public AuthAccountRow findById(Long userId) {
    return authAccountMapper.selectById(userId);
  }

  @Override
  public List<String> findActiveRoles(Long userId) {
    return authAccountMapper.selectActiveRoles(userId);
  }

  @Override
  public Long findUserRoleId() {
    return authAccountMapper.selectUserRoleId();
  }

  @Override
  public int createAccount(AuthRegisterRow account) {
    return authAccountMapper.insertAccount(account);
  }

  @Override
  public int addUserRole(
    Long userId,
    Long roleId
  ) {
    return authAccountMapper.insertUserRole(
      userId,
      roleId
    );
  }
}
