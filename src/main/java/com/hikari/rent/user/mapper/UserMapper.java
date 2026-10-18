package com.hikari.rent.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hikari.rent.user.entity.User;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper extends BaseMapper<User> {

  User selectUserById(@Param("id") Long id);
}
