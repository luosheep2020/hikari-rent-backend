package com.hikari.rent.company.service;

import com.hikari.rent.company.dto.AdminDepartmentSaveRequest;
import com.hikari.rent.company.dto.AdminDepartmentStatusRequest;
import com.hikari.rent.company.vo.AdminDepartmentCreatedVO;
import com.hikari.rent.company.vo.AdminDepartmentTreeVO;

import java.util.List;

public interface AdminDepartmentService {

  List<AdminDepartmentTreeVO> getTree(Long companyId);

  AdminDepartmentCreatedVO create(
    AdminDepartmentSaveRequest request
  );

  void update(
    Long departmentId,
    AdminDepartmentSaveRequest request
  );

  void updateStatus(
    Long departmentId,
    AdminDepartmentStatusRequest request
  );
}
