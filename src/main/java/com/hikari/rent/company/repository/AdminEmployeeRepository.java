package com.hikari.rent.company.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hikari.rent.company.dto.AdminEmployeePageRequest;
import com.hikari.rent.company.entity.Employee;

public interface AdminEmployeeRepository {

  IPage<Employee> searchPage(
    AdminEmployeePageRequest request,
    Long companyId
  );

  Long findCompanyId(Long employeeId);

  Employee findDetail(
    Long employeeId,
    Long companyId
  );

  Employee findDetailForUpdate(
    Long employeeId,
    Long companyId
  );

  boolean activeDepartmentExists(
    Long departmentId,
    Long companyId
  );

  boolean activePositionExists(
    Long positionId,
    Long companyId
  );

  Long findActiveAgentRoleIdForShare();

  void insert(Employee employee);

  void insertAgentRole(Long employeeId, Long roleId);

  void update(Employee employee);

  void updateStatus(
    Long employeeId,
    Long companyId,
    int status
  );

  void enqueueRentalSyncTasks(Long employeeId);
}
