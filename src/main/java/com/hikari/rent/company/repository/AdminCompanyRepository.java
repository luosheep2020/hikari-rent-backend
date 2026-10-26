package com.hikari.rent.company.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hikari.rent.company.dto.AdminCompanyPageRequest;
import com.hikari.rent.company.entity.Company;

public interface AdminCompanyRepository {

  IPage<Company> searchPage(
    AdminCompanyPageRequest request
  );

  Company findById(Long companyId);

  Company findByIdForUpdate(Long companyId);

  void insert(Company company);

  void update(Company company);

  void updateStatus(Long companyId, int status);

  void enqueueRelatedRentalSyncTasks(Long companyId);
}
