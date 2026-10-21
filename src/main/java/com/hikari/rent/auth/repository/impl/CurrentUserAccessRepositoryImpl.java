package com.hikari.rent.auth.repository.impl;

import com.hikari.rent.auth.mapper.CurrentUserAccessMapper;
import com.hikari.rent.auth.repository.CurrentUserAccessRepository;
import com.hikari.rent.auth.repository.model.CurrentUserRoleRow;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CurrentUserAccessRepositoryImpl
  implements CurrentUserAccessRepository {

  @Resource
  private CurrentUserAccessMapper currentUserAccessMapper;

  @Override
  public List<CurrentUserRoleRow> findUserRoles(
    Long userId
  ) {
    return currentUserAccessMapper.selectUserRoles(
      userId
    );
  }
}
