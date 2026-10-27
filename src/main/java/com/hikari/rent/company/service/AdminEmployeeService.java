package com.hikari.rent.company.service;

import com.hikari.rent.common.result.PageResult;
import com.hikari.rent.company.dto.AdminEmployeeCreateRequest;
import com.hikari.rent.company.dto.AdminEmployeePageRequest;
import com.hikari.rent.company.dto.AdminEmployeeStatusRequest;
import com.hikari.rent.company.dto.AdminEmployeeUpdateRequest;
import com.hikari.rent.company.vo.AdminEmployeeCreatedVO;
import com.hikari.rent.company.vo.AdminEmployeeVO;

public interface AdminEmployeeService {

  PageResult<AdminEmployeeVO> searchPage(
    AdminEmployeePageRequest request
  );

  AdminEmployeeVO getDetail(Long employeeId);

  AdminEmployeeCreatedVO create(
    AdminEmployeeCreateRequest request
  );

  void update(
    Long employeeId,
    AdminEmployeeUpdateRequest request
  );

  void updateStatus(
    Long employeeId,
    AdminEmployeeStatusRequest request
  );
}
