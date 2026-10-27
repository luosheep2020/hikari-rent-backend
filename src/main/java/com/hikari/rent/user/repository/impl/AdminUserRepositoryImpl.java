package com.hikari.rent.user.repository.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hikari.rent.user.dto.AdminUserPageRequest;
import com.hikari.rent.user.mapper.AdminUserMapper;
import com.hikari.rent.user.repository.AdminUserRepository;
import com.hikari.rent.user.repository.model.AdminUserRoleRow;
import com.hikari.rent.user.repository.model.AdminUserRow;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AdminUserRepositoryImpl
  implements AdminUserRepository {

  @Resource
  private AdminUserMapper adminUserMapper;

  @Override
  public IPage<AdminUserRow> searchPage(
    AdminUserPageRequest request
  ) {
    Page<AdminUserRow> page = new Page<>(
      request.getPage(),
      request.getPageSize()
    );

    page.setOptimizeCountSql(false);

    return adminUserMapper.searchPage(page, request);
  }

  @Override
  public AdminUserRow findById(Long userId) {
    return adminUserMapper.selectById(userId);
  }

  @Override
  public AdminUserRow findByIdForUpdate(Long userId) {
    return adminUserMapper.selectByIdForUpdate(userId);
  }

  @Override
  public List<AdminUserRoleRow> findRolesByUserIds(
    List<Long> userIds
  ) {
    if (userIds == null || userIds.isEmpty()) {
      return List.of();
    }

    return adminUserMapper.selectRolesByUserIds(userIds);
  }

  @Override
  public void updateStatus(Long userId, int status) {
    int updated = adminUserMapper.updateStatus(userId, status);

    if (updated != 1) {
      throw new IllegalStateException(
        "User status update did not affect exactly one account"
      );
    }
  }

  @Override
  public void enqueueRentalSyncTasks(Long userId) {
    adminUserMapper.enqueueRentalSyncTasks(userId);
  }
}
