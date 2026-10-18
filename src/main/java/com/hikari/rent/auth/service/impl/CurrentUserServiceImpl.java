package com.hikari.rent.auth.service.impl;

import com.hikari.rent.auth.service.CurrentUserService;
import com.hikari.rent.common.enums.ErrorCode;
import com.hikari.rent.common.exception.BusinessException;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("!dev | prod")
public class CurrentUserServiceImpl
  implements CurrentUserService {

  @Override
  public Long requireUserId() {
    throw new BusinessException(
      ErrorCode.AUTHENTICATION_REQUIRED
    );
  }
}
