package com.hikari.rent.auth.model;

import com.hikari.rent.auth.vo.AuthUserVO;

import java.security.Principal;

public record AuthPrincipal(
  Long userId,
  String sessionId,
  AuthUserVO user
) implements Principal {

  @Override
  public String getName() {
    return userId.toString();
  }
}
