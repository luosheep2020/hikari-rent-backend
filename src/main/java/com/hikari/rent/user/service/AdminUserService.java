package com.hikari.rent.user.service;

import com.hikari.rent.common.result.PageResult;
import com.hikari.rent.user.dto.AdminUserPageRequest;
import com.hikari.rent.user.dto.AdminUserStatusRequest;
import com.hikari.rent.user.vo.AdminUserVO;

public interface AdminUserService {

  PageResult<AdminUserVO> searchPage(
    AdminUserPageRequest request
  );

  AdminUserVO getDetail(Long userId);

  void updateStatus(
    Long userId,
    AdminUserStatusRequest request
  );
}
