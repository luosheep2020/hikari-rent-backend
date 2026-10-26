package com.hikari.rent.company.service;

import com.hikari.rent.common.result.PageResult;
import com.hikari.rent.company.dto.AdminCompanyPageRequest;
import com.hikari.rent.company.dto.AdminCompanySaveRequest;
import com.hikari.rent.company.dto.AdminCompanyStatusRequest;
import com.hikari.rent.company.vo.AdminCompanyCreatedVO;
import com.hikari.rent.company.vo.AdminCompanyVO;

public interface AdminCompanyService {

  PageResult<AdminCompanyVO> searchPage(
    AdminCompanyPageRequest request
  );

  AdminCompanyVO getDetail(Long companyId);

  AdminCompanyCreatedVO create(
    AdminCompanySaveRequest request
  );

  void update(
    Long companyId,
    AdminCompanySaveRequest request
  );

  void updateStatus(
    Long companyId,
    AdminCompanyStatusRequest request
  );
}
