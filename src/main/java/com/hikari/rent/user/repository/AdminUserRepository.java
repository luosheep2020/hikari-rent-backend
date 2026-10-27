package com.hikari.rent.user.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hikari.rent.user.dto.AdminUserPageRequest;
import com.hikari.rent.user.repository.model.AdminUserRoleRow;
import com.hikari.rent.user.repository.model.AdminUserRow;

import java.util.List;

public interface AdminUserRepository {

  IPage<AdminUserRow> searchPage(
    AdminUserPageRequest request
  );

  AdminUserRow findById(Long userId);

  AdminUserRow findByIdForUpdate(Long userId);

  List<AdminUserRoleRow> findRolesByUserIds(
    List<Long> userIds
  );

  void updateStatus(Long userId, int status);

  void enqueueRentalSyncTasks(Long userId);
}
