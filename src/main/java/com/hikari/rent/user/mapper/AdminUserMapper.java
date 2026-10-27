package com.hikari.rent.user.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hikari.rent.user.dto.AdminUserPageRequest;
import com.hikari.rent.user.repository.model.AdminUserRoleRow;
import com.hikari.rent.user.repository.model.AdminUserRow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminUserMapper {

  IPage<AdminUserRow> searchPage(
    IPage<AdminUserRow> page,
    @Param("query") AdminUserPageRequest query
  );

  AdminUserRow selectById(
    @Param("userId") Long userId
  );

  AdminUserRow selectByIdForUpdate(
    @Param("userId") Long userId
  );

  List<AdminUserRoleRow> selectRolesByUserIds(
    @Param("userIds") List<Long> userIds
  );

  int updateStatus(
    @Param("userId") Long userId,
    @Param("status") int status
  );

  int enqueueRentalSyncTasks(
    @Param("userId") Long userId
  );
}
