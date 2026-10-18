package com.hikari.rent.user.repository.impl;


import com.baomidou.mybatisplus.spring.repository.CrudRepository;
import com.hikari.rent.user.entity.User;
import com.hikari.rent.user.mapper.UserMapper;
import com.hikari.rent.user.repository.UserRepository;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepositoryImpl
  extends CrudRepository<UserMapper, User>
  implements UserRepository {

  @Override
  public Optional<User> findUserById(Long userId) {
    if (userId == null) {
      return Optional.empty();
    }

    return Optional.ofNullable(
      baseMapper.selectUserById(userId)
    );
  }
}
