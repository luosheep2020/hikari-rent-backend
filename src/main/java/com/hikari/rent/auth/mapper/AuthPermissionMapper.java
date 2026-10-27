package com.hikari.rent.auth.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AuthPermissionMapper {

  List<String> selectPermissionCodes(
    @Param("userId") Long userId
  );

  List<String> selectAllPermissionCodes();
}
