package com.hikari.rent.user.repository;

import com.baomidou.mybatisplus.extension.repository.IRepository;
import com.hikari.rent.user.entity.User;

import java.util.Optional;

public interface UserRepository extends IRepository<User> {

  Optional<User> findUserById(Long userId);
}
