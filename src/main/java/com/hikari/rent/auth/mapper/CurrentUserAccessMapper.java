package com.hikari.rent.auth.mapper;

import com.hikari.rent.auth.repository.model.CurrentUserRoleRow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CurrentUserAccessMapper {

  List<CurrentUserRoleRow> selectUserRoles(
    @Param("userId") Long userId
  );
}
