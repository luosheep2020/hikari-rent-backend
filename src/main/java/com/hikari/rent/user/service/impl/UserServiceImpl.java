package com.hikari.rent.user.service.impl;

import com.hikari.rent.common.enums.ErrorCode;
import com.hikari.rent.common.exception.BusinessException;

import com.hikari.rent.user.entity.User;
import com.hikari.rent.user.repository.UserRepository;
import com.hikari.rent.user.service.UserService;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

  @Resource
  private UserRepository userRepository;

  @Override
  @Transactional(readOnly = true)
  public User requireActiveUser(Long userId) {
    if (userId == null || userId <= 0) {
      throw new BusinessException(
        ErrorCode.AUTHENTICATION_REQUIRED
      );
    }

    User user = userRepository.findUserById(userId)
      .orElseThrow(() -> new BusinessException(
        ErrorCode.USER_NOT_FOUND
      ));

    if (!Integer.valueOf(1).equals(user.getStatus())) {
      throw new BusinessException(
        ErrorCode.ACCOUNT_DISABLED
      );
    }

    return user;
  }
}
