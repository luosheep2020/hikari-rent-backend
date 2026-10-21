package com.hikari.rent.auth.repository;

import com.hikari.rent.auth.repository.model.AuthAccountRow;
import com.hikari.rent.auth.repository.model.AuthRegisterRow;

import java.util.List;

public interface AuthAccountRepository {

  AuthAccountRow findByEmail(String email);

  AuthAccountRow findById(Long userId);

  List<String> findActiveRoles(Long userId);

  Long findUserRoleId();

  int createAccount(AuthRegisterRow account);

  int addUserRole(Long userId, Long roleId);
}
