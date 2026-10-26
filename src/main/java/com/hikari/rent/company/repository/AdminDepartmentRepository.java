package com.hikari.rent.company.repository;

import com.hikari.rent.company.entity.Department;

import java.util.List;

public interface AdminDepartmentRepository {

  Department findById(Long departmentId);

  List<Department> findByCompanyId(Long companyId);

  List<Department> findByCompanyIdForUpdate(Long companyId);

  void insert(Department department);

  void update(Department department);

  void updateStatus(
    Long departmentId,
    Long companyId,
    int status
  );

  boolean hasActiveUsers(
    Long departmentId,
    Long companyId
  );
}
