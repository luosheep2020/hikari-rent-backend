package com.hikari.rent.auth.model;

import java.util.Set;

public record CurrentUserContext(
  Long userId,
  Long companyId,
  boolean companyActive,
  Set<Role> roles
) {

  public CurrentUserContext {
    roles = Set.copyOf(roles);
  }

  public boolean hasRole(
    String code,
    String scope
  ) {
    return roles.contains(
      new Role(code, scope)
    );
  }

  public record Role(
    String code,
    String scope
  ) {
  }
}
