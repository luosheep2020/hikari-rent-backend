package com.hikari.rent.company.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hikari.rent.company.dto.AdminEmployeePageRequest;
import com.hikari.rent.company.entity.Employee;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AdminEmployeeMapper {

  IPage<Employee> searchPage(
    IPage<Employee> page,
    @Param("query") AdminEmployeePageRequest query,
    @Param("companyId") Long companyId
  );

  Long selectCompanyId(
    @Param("employeeId") Long employeeId
  );

  Employee selectDetail(
    @Param("employeeId") Long employeeId,
    @Param("companyId") Long companyId
  );

  Employee selectDetailForUpdate(
    @Param("employeeId") Long employeeId,
    @Param("companyId") Long companyId
  );

  Long selectActiveDepartmentForShare(
    @Param("departmentId") Long departmentId,
    @Param("companyId") Long companyId
  );

  Long selectActivePositionForShare(
    @Param("positionId") Long positionId,
    @Param("companyId") Long companyId
  );

  Long selectActiveAgentRoleForShare();

  int insert(
    @Param("employee") Employee employee
  );

  int insertAgentRole(
    @Param("employeeId") Long employeeId,
    @Param("roleId") Long roleId
  );

  int update(
    @Param("employee") Employee employee
  );

  int updateStatus(
    @Param("employeeId") Long employeeId,
    @Param("companyId") Long companyId,
    @Param("status") int status
  );

  int enqueueRentalSyncTasks(
    @Param("employeeId") Long employeeId
  );
}
