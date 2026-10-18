package com.hikari.rent.user.service;

import com.hikari.rent.user.entity.User;

public interface UserService {

  User requireActiveUser(Long userId);
}
