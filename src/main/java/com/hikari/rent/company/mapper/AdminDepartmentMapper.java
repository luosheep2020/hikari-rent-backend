package com.hikari.rent.company.mapper;

import com.hikari.rent.company.entity.Department;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminDepartmentMapper {

  Department selectById(
    @Param("departmentId") Long departmentId
  );

  List<Department> selectByCompanyId(
    @Param("companyId") Long companyId
  );

  List<Department> selectByCompanyIdForUpdate(
    @Param("companyId") Long companyId
  );

  int insert(
    @Param("department") Department department
  );

  int update(
    @Param("department") Department department
  );

  int updateStatus(
    @Param("departmentId") Long departmentId,
    @Param("companyId") Long companyId,
    @Param("status") int status
  );

  int countActiveUsers(
    @Param("departmentId") Long departmentId,
    @Param("companyId") Long companyId
  );
}
