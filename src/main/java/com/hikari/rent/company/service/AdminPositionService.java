package com.hikari.rent.company.service;

import com.hikari.rent.company.dto.AdminPositionSaveRequest;
import com.hikari.rent.company.dto.AdminPositionStatusRequest;
import com.hikari.rent.company.vo.AdminPositionCreatedVO;
import com.hikari.rent.company.vo.AdminPositionVO;

import java.util.List;

public interface AdminPositionService {

  List<AdminPositionVO> getList(Long companyId);

  AdminPositionCreatedVO create(
    AdminPositionSaveRequest request
  );

  void update(
    Long positionId,
    AdminPositionSaveRequest request
  );

  void updateStatus(
    Long positionId,
    AdminPositionStatusRequest request
  );
}
