package com.hikari.rent.company.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hikari.rent.common.enums.ErrorCode;
import com.hikari.rent.common.exception.BusinessException;
import com.hikari.rent.common.result.PageResult;
import com.hikari.rent.company.dto.AdminEmployeeCreateRequest;
import com.hikari.rent.company.dto.AdminEmployeePageRequest;
import com.hikari.rent.company.dto.AdminEmployeeStatusRequest;
import com.hikari.rent.company.dto.AdminEmployeeUpdateRequest;
import com.hikari.rent.company.entity.Company;
import com.hikari.rent.company.entity.Employee;
import com.hikari.rent.company.repository.AdminCompanyRepository;
import com.hikari.rent.company.repository.AdminEmployeeRepository;
import com.hikari.rent.company.service.AdminEmployeeService;
import com.hikari.rent.company.service.CompanyAccessService;
import com.hikari.rent.company.vo.AdminEmployeeCreatedVO;
import com.hikari.rent.company.vo.AdminEmployeeVO;
import jakarta.annotation.Resource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Objects;

@Service
@PreAuthorize(
  "hasAnyRole('PLATFORM_ADMIN', 'COMPANY_ADMIN') "
    + "and hasAuthority('staff:manage')"
)
public class AdminEmployeeServiceImpl
  implements AdminEmployeeService {

  @Resource
  private CompanyAccessService companyAccessService;

  @Resource
  private AdminCompanyRepository adminCompanyRepository;

  @Resource
  private AdminEmployeeRepository adminEmployeeRepository;

  @Resource
  private PasswordEncoder passwordEncoder;

  @Override
  @Transactional(readOnly = true)
  public PageResult<AdminEmployeeVO> searchPage(
    AdminEmployeePageRequest request
  ) {
    Long companyId =
      companyAccessService.requireCompanyId(
        request.getCompanyId()
      );

    requireCompanyExists(companyId);

    IPage<Employee> page =
      adminEmployeeRepository.searchPage(
        request,
        companyId
      );

    return new PageResult<>(
      page.getCurrent(),
      page.getSize(),
      page.getTotal(),
      page.getRecords()
        .stream()
        .map(this::toVO)
        .toList()
    );
  }

  @Override
  @Transactional(readOnly = true)
  public AdminEmployeeVO getDetail(Long employeeId) {
    Long companyId = resolveEmployeeCompanyId(employeeId);

    Employee employee =
      adminEmployeeRepository.findDetail(
        employeeId,
        companyId
      );

    if (employee == null) {
      throw employeeNotFound();
    }

    return toVO(employee);
  }

  @Override
  @Transactional
  public AdminEmployeeCreatedVO create(
    AdminEmployeeCreateRequest request
  ) {
    Long companyId =
      companyAccessService.requireCompanyId(
        request.getCompanyId()
      );

    lockActiveCompany(companyId);

    validateOrganization(
      companyId,
      request.getDepartmentId(),
      request.getPositionId()
    );

    Long agentRoleId =
      adminEmployeeRepository.findActiveAgentRoleIdForShare();

    if (agentRoleId == null) {
      throw new BusinessException(
        ErrorCode.ROLE_NOT_FOUND,
        "An active AGENT role with SELF scope is required"
      );
    }

    Employee employee = new Employee();
    employee.setCompanyId(companyId);

    employee.setEmail(
      request.getEmail()
        .strip()
        .toLowerCase(Locale.ROOT)
    );

    // Do not trim or normalize passwords.
    employee.setPasswordHash(
      passwordEncoder.encode(request.getPassword())
    );

    applyProfile(employee, request);

    try {
      adminEmployeeRepository.insert(employee);
    } catch (DuplicateKeyException ex) {
      throw new BusinessException(
        ErrorCode.EMAIL_ALREADY_REGISTERED
      );
    }

    // Account creation and role assignment must commit together.
    adminEmployeeRepository.insertAgentRole(
      employee.getId(),
      agentRoleId
    );

    return new AdminEmployeeCreatedVO(
      employee.getId().toString()
    );
  }

  @Override
  @Transactional
  public void update(
    Long employeeId,
    AdminEmployeeUpdateRequest request
  ) {
    Employee employee = requireEmployeeForUpdate(employeeId);

    validateOrganization(
      employee.getCompanyId(),
      request.getDepartmentId(),
      request.getPositionId()
    );

    applyProfile(employee, request);

    adminEmployeeRepository.update(employee);

    adminEmployeeRepository.enqueueRentalSyncTasks(employeeId);
  }

  @Override
  @Transactional
  public void updateStatus(
    Long employeeId,
    AdminEmployeeStatusRequest request
  ) {
    if (
      request == null
        || request.getStatus() == null
        || (request.getStatus() != 0 && request.getStatus() != 1)
    ) {
      throw new BusinessException(ErrorCode.BAD_REQUEST);
    }

    Employee employee = requireEmployeeForUpdate(employeeId);

    if (
      Objects.equals(
        employee.getStatus(),
        request.getStatus()
      )
    ) {
      return;
    }

    // Disabled departments or positions must not be reactivated indirectly.
    if (request.getStatus() == 1) {
      validateOrganization(
        employee.getCompanyId(),
        employee.getDepartmentId(),
        employee.getPositionId()
      );
    }

    adminEmployeeRepository.updateStatus(
      employeeId,
      employee.getCompanyId(),
      request.getStatus()
    );

    adminEmployeeRepository.enqueueRentalSyncTasks(employeeId);
  }

  private Long resolveEmployeeCompanyId(Long employeeId) {
    if (employeeId == null || employeeId <= 0) {
      throw new BusinessException(ErrorCode.BAD_REQUEST);
    }

    Long companyId =
      adminEmployeeRepository.findCompanyId(employeeId);

    if (companyId == null) {
      throw employeeNotFound();
    }

    return companyAccessService.requireCompanyId(companyId);
  }

  private Employee requireEmployeeForUpdate(Long employeeId) {
    Long companyId = resolveEmployeeCompanyId(employeeId);

    lockActiveCompany(companyId);

    Employee employee =
      adminEmployeeRepository.findDetailForUpdate(
        employeeId,
        companyId
      );

    if (employee == null) {
      throw employeeNotFound();
    }

    return employee;
  }

  private void requireCompanyExists(Long companyId) {
    if (adminCompanyRepository.findById(companyId) == null) {
      throw new BusinessException(ErrorCode.COMPANY_NOT_FOUND);
    }
  }

  private void lockActiveCompany(Long companyId) {
    Company company =
      adminCompanyRepository.findByIdForUpdate(companyId);

    if (company == null) {
      throw new BusinessException(ErrorCode.COMPANY_NOT_FOUND);
    }

    if (!Integer.valueOf(1).equals(company.getStatus())) {
      throw new BusinessException(ErrorCode.COMPANY_DISABLED);
    }
  }

  private void validateOrganization(
    Long companyId,
    Long departmentId,
    Long positionId
  ) {
    if (
      departmentId != null
        && !adminEmployeeRepository.activeDepartmentExists(
        departmentId,
        companyId
      )
    ) {
      throw new BusinessException(
        ErrorCode.ORGANIZATION_COMPANY_MISMATCH,
        "Department must be active and belong to the company"
      );
    }

    if (
      positionId != null
        && !adminEmployeeRepository.activePositionExists(
        positionId,
        companyId
      )
    ) {
      throw new BusinessException(
        ErrorCode.ORGANIZATION_COMPANY_MISMATCH,
        "Position must be active and belong to the company"
      );
    }
  }

  private void applyProfile(
    Employee employee,
    AdminEmployeeUpdateRequest request
  ) {
    employee.setNickname(request.getNickname().strip());
    employee.setPhone(normalize(request.getPhone()));
    employee.setDepartmentId(request.getDepartmentId());
    employee.setPositionId(request.getPositionId());
  }

  private String normalize(String value) {
    if (value == null) {
      return null;
    }

    String normalized = value.strip();

    return normalized.isBlank() ? null : normalized;
  }

  private AdminEmployeeVO toVO(Employee employee) {
    return new AdminEmployeeVO(
      employee.getId().toString(),
      employee.getEmail(),
      employee.getNickname(),
      employee.getPhone(),
      employee.getCompanyId().toString(),
      employee.getCompanyName(),
      toId(employee.getDepartmentId()),
      employee.getDepartmentName(),
      toId(employee.getPositionId()),
      employee.getPositionName(),
      employee.getStatus(),
      employee.getLastLoginAt(),
      employee.getCreatedAt(),
      employee.getUpdatedAt()
    );
  }

  private String toId(Long id) {
    return id == null ? null : id.toString();
  }

  private BusinessException employeeNotFound() {
    return new BusinessException(
      ErrorCode.USER_NOT_FOUND,
      "Manageable agent account not found"
    );
  }
}
