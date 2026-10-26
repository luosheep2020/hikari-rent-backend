package com.hikari.rent.company.repository.impl;

import com.hikari.rent.company.entity.Department;
import com.hikari.rent.company.mapper.AdminDepartmentMapper;
import com.hikari.rent.company.repository.AdminDepartmentRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AdminDepartmentRepositoryImpl
  implements AdminDepartmentRepository {

  @Resource
  private AdminDepartmentMapper adminDepartmentMapper;

  @Override
  public Department findById(Long departmentId) {
    return adminDepartmentMapper.selectById(departmentId);
  }

  @Override
  public List<Department> findByCompanyId(Long companyId) {
    return adminDepartmentMapper.selectByCompanyId(companyId);
  }

  @Override
  public List<Department> findByCompanyIdForUpdate(Long companyId) {
    return adminDepartmentMapper.selectByCompanyIdForUpdate(
      companyId
    );
  }

  @Override
  public void insert(Department department) {
    int inserted = adminDepartmentMapper.insert(department);

    if (inserted != 1 || department.getId() == null) {
      throw new IllegalStateException(
        "Department insert did not return a generated ID"
      );
    }
  }

  @Override
  public void update(Department department) {
    adminDepartmentMapper.update(department);
  }

  @Override
  public void updateStatus(
    Long departmentId,
    Long companyId,
    int status
  ) {
    adminDepartmentMapper.updateStatus(
      departmentId,
      companyId,
      status
    );
  }

  @Override
  public boolean hasActiveUsers(
    Long departmentId,
    Long companyId
  ) {
    return adminDepartmentMapper.countActiveUsers(
      departmentId,
      companyId
    ) > 0;
  }
}
