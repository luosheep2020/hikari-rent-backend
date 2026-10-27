package com.hikari.rent.railway.service;

import com.hikari.rent.common.result.PageResult;
import com.hikari.rent.railway.dto.AdminStationCreateRequest;
import com.hikari.rent.railway.dto.AdminStationPageRequest;
import com.hikari.rent.railway.dto.AdminStationStatusRequest;
import com.hikari.rent.railway.dto.AdminStationUpdateRequest;
import com.hikari.rent.railway.vo.AdminStationCreatedVO;
import com.hikari.rent.railway.vo.AdminStationVO;

public interface AdminStationService {

  PageResult<AdminStationVO> searchPage(
    AdminStationPageRequest request
  );

  AdminStationVO getDetail(Long stationId);

  AdminStationCreatedVO create(
    AdminStationCreateRequest request
  );

  void update(
    Long stationId,
    AdminStationUpdateRequest request
  );

  void updateStatus(
    Long stationId,
    AdminStationStatusRequest request
  );
}
