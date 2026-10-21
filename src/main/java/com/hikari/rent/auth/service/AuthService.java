package com.hikari.rent.auth.service;

import com.hikari.rent.auth.dto.LoginRequest;
import com.hikari.rent.auth.dto.RegisterRequest;
import com.hikari.rent.auth.vo.AuthUserVO;
import com.hikari.rent.auth.vo.LoginVO;

public interface AuthService {

  LoginVO login(LoginRequest request);

  void register(RegisterRequest request);

  AuthUserVO me();

  void logout();
}
