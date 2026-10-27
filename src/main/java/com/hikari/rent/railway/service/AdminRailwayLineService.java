package com.hikari.rent.railway.service;

import com.hikari.rent.common.result.PageResult;
import com.hikari.rent.railway.dto.AdminRailwayLineCreateRequest;
import com.hikari.rent.railway.dto.AdminRailwayLinePageRequest;
import com.hikari.rent.railway.dto.AdminRailwayLineStatusRequest;
import com.hikari.rent.railway.dto.AdminRailwayLineUpdateRequest;
import com.hikari.rent.railway.vo.AdminRailwayLineCreatedVO;
import com.hikari.rent.railway.vo.AdminRailwayLineVO;

public interface AdminRailwayLineService {

  PageResult<AdminRailwayLineVO> searchPage(
    AdminRailwayLinePageRequest request
  );

  AdminRailwayLineVO getDetail(Long lineId);

  AdminRailwayLineCreatedVO create(
    AdminRailwayLineCreateRequest request
  );

  void update(
    Long lineId,
    AdminRailwayLineUpdateRequest request
  );

  void updateStatus(
    Long lineId,
    AdminRailwayLineStatusRequest request
  );
}
