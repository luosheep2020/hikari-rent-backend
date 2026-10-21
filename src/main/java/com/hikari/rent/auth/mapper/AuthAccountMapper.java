package com.hikari.rent.auth.mapper;

import com.hikari.rent.auth.repository.model.AuthAccountRow;
import com.hikari.rent.auth.repository.model.AuthRegisterRow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AuthAccountMapper {

  AuthAccountRow selectByEmail(
    @Param("email") String email
  );

  AuthAccountRow selectById(
    @Param("userId") Long userId
  );

  List<String> selectActiveRoles(
    @Param("userId") Long userId
  );

  Long selectUserRoleId();

  int insertAccount(
    @Param("account") AuthRegisterRow account
  );

  int insertUserRole(
    @Param("userId") Long userId,
    @Param("roleId") Long roleId
  );
}
