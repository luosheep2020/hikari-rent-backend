package com.hikari.rent.auth.service;

import com.hikari.rent.auth.model.AuthPrincipal;
import com.hikari.rent.common.enums.ErrorCode;
import com.hikari.rent.common.exception.BusinessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

  public Long requireUserId() {
    return requirePrincipal().userId();
  }

  public AuthPrincipal requirePrincipal() {
    Authentication authentication =
      SecurityContextHolder.getContext()
        .getAuthentication();

    if (
      authentication == null
        || !authentication.isAuthenticated()
        || !(authentication.getPrincipal()
        instanceof AuthPrincipal principal)
    ) {
      throw new BusinessException(
        ErrorCode.AUTHENTICATION_REQUIRED
      );
    }

    return principal;
  }
}
