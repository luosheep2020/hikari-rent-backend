package com.hikari.rent.auth.service.impl;

import com.hikari.rent.auth.service.CurrentUserService;
import com.hikari.rent.user.service.UserService;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("dev & !prod")
public class DevCurrentUserService
  implements CurrentUserService {

  @Value("${app.dev.user-id:0}")
  private Long userId;

  @Resource
  private UserService userService;

  @PostConstruct
  public void validateConfiguration() {
    if (userId == null || userId <= 0) {
      throw new IllegalStateException(
        "A positive app.dev.user-id is required in the dev profile"
      );
    }
  }

  @Override
  public Long requireUserId() {
    return userService.requireActiveUser(userId).getId();
  }
}
