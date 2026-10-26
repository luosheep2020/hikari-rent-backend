package com.hikari.rent.company.repository.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hikari.rent.company.dto.AdminCompanyPageRequest;
import com.hikari.rent.company.entity.Company;
import com.hikari.rent.company.mapper.AdminCompanyMapper;
import com.hikari.rent.company.repository.AdminCompanyRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Repository;

@Repository
public class AdminCompanyRepositoryImpl
  implements AdminCompanyRepository {

  @Resource
  private AdminCompanyMapper adminCompanyMapper;

  @Override
  public IPage<Company> searchPage(
    AdminCompanyPageRequest request
  ) {
    Page<Company> page = new Page<>(
      request.getPage(),
      request.getPageSize()
    );

    page.setOptimizeCountSql(false);

    return adminCompanyMapper.searchPage(page, request);
  }

  @Override
  public Company findById(Long companyId) {
    return adminCompanyMapper.selectById(companyId);
  }

  @Override
  public Company findByIdForUpdate(Long companyId) {
    return adminCompanyMapper.selectByIdForUpdate(companyId);
  }

  @Override
  public void insert(Company company) {
    int inserted = adminCompanyMapper.insert(company);

    if (inserted != 1 || company.getId() == null) {
      throw new IllegalStateException(
        "Company insert did not return a generated ID"
      );
    }
  }

  @Override
  public void update(Company company) {
    adminCompanyMapper.update(company);
  }

  @Override
  public void updateStatus(Long companyId, int status) {
    adminCompanyMapper.updateStatus(companyId, status);
  }

  @Override
  public void enqueueRelatedRentalSyncTasks(Long companyId) {
    adminCompanyMapper.enqueueRelatedRentalSyncTasks(companyId);
  }
}
