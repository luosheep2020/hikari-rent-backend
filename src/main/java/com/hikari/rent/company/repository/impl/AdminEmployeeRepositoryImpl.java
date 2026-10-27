package com.hikari.rent.company.repository.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hikari.rent.company.dto.AdminEmployeePageRequest;
import com.hikari.rent.company.entity.Employee;
import com.hikari.rent.company.mapper.AdminEmployeeMapper;
import com.hikari.rent.company.repository.AdminEmployeeRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Repository;

@Repository
public class AdminEmployeeRepositoryImpl
  implements AdminEmployeeRepository {

  @Resource
  private AdminEmployeeMapper adminEmployeeMapper;

  @Override
  public IPage<Employee> searchPage(
    AdminEmployeePageRequest request,
    Long companyId
  ) {
    Page<Employee> page = new Page<>(
      request.getPage(),
      request.getPageSize()
    );

    page.setOptimizeCountSql(false);

    return adminEmployeeMapper.searchPage(
      page,
      request,
      companyId
    );
  }

  @Override
  public Long findCompanyId(Long employeeId) {
    return adminEmployeeMapper.selectCompanyId(employeeId);
  }

  @Override
  public Employee findDetail(
    Long employeeId,
    Long companyId
  ) {
    return adminEmployeeMapper.selectDetail(
      employeeId,
      companyId
    );
  }

  @Override
  public Employee findDetailForUpdate(
    Long employeeId,
    Long companyId
  ) {
    return adminEmployeeMapper.selectDetailForUpdate(
      employeeId,
      companyId
    );
  }

  @Override
  public boolean activeDepartmentExists(
    Long departmentId,
    Long companyId
  ) {
    return adminEmployeeMapper.selectActiveDepartmentForShare(
      departmentId,
      companyId
    ) != null;
  }

  @Override
  public boolean activePositionExists(
    Long positionId,
    Long companyId
  ) {
    return adminEmployeeMapper.selectActivePositionForShare(
      positionId,
      companyId
    ) != null;
  }

  @Override
  public Long findActiveAgentRoleIdForShare() {
    return adminEmployeeMapper.selectActiveAgentRoleForShare();
  }

  @Override
  public void insert(Employee employee) {
    int inserted = adminEmployeeMapper.insert(employee);

    if (inserted != 1 || employee.getId() == null) {
      throw new IllegalStateException(
        "Employee insert did not return a generated ID"
      );
    }
  }

  @Override
  public void insertAgentRole(Long employeeId, Long roleId) {
    int inserted = adminEmployeeMapper.insertAgentRole(
      employeeId,
      roleId
    );

    if (inserted != 1) {
      throw new IllegalStateException(
        "Employee agent role was not assigned"
      );
    }
  }

  @Override
  public void update(Employee employee) {
    adminEmployeeMapper.update(employee);
  }

  @Override
  public void updateStatus(
    Long employeeId,
    Long companyId,
    int status
  ) {
    adminEmployeeMapper.updateStatus(
      employeeId,
      companyId,
      status
    );
  }

  @Override
  public void enqueueRentalSyncTasks(Long employeeId) {
    adminEmployeeMapper.enqueueRentalSyncTasks(employeeId);
  }
}
