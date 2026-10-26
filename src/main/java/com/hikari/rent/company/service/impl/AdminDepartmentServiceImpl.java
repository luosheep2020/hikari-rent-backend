package com.hikari.rent.company.service.impl;

import com.hikari.rent.common.enums.ErrorCode;
import com.hikari.rent.common.exception.BusinessException;
import com.hikari.rent.company.dto.AdminDepartmentSaveRequest;
import com.hikari.rent.company.dto.AdminDepartmentStatusRequest;
import com.hikari.rent.company.entity.Company;
import com.hikari.rent.company.entity.Department;
import com.hikari.rent.company.repository.AdminCompanyRepository;
import com.hikari.rent.company.repository.AdminDepartmentRepository;
import com.hikari.rent.company.service.AdminDepartmentService;
import com.hikari.rent.company.service.CompanyAccessService;
import com.hikari.rent.company.vo.AdminDepartmentCreatedVO;
import com.hikari.rent.company.vo.AdminDepartmentTreeVO;
import jakarta.annotation.Resource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Service
public class AdminDepartmentServiceImpl
  implements AdminDepartmentService {

  @Resource
  private CompanyAccessService companyAccessService;

  @Resource
  private AdminCompanyRepository adminCompanyRepository;

  @Resource
  private AdminDepartmentRepository adminDepartmentRepository;

  @Override
  @Transactional(readOnly = true)
  public List<AdminDepartmentTreeVO> getTree(Long companyId) {
    Long allowedCompanyId =
      companyAccessService.requireCompanyId(companyId);

    if (adminCompanyRepository.findById(allowedCompanyId) == null) {
      throw new BusinessException(ErrorCode.COMPANY_NOT_FOUND);
    }

    List<Department> departments =
      adminDepartmentRepository.findByCompanyId(allowedCompanyId);

    Map<Long, Department> departmentMap = toMap(departments);
    Map<Long, AdminDepartmentTreeVO> nodes = new LinkedHashMap<>();

    for (Department department : departments) {
      // Detect invalid legacy parent chains before assembling the tree.
      validateParent(
        department.getId(),
        department.getParentId(),
        departmentMap,
        false
      );

      nodes.put(department.getId(), toTreeVO(department));
    }

    List<AdminDepartmentTreeVO> roots = new ArrayList<>();

    for (Department department : departments) {
      AdminDepartmentTreeVO node = nodes.get(department.getId());

      if (department.getParentId() == null) {
        roots.add(node);
      } else {
        nodes.get(department.getParentId())
          .getChildren()
          .add(node);
      }
    }

    return roots;
  }

  @Override
  @Transactional
  public AdminDepartmentCreatedVO create(
    AdminDepartmentSaveRequest request
  ) {
    Long companyId =
      companyAccessService.requireCompanyId(request.getCompanyId());

    lockActiveCompany(companyId);

    List<Department> departments =
      adminDepartmentRepository.findByCompanyIdForUpdate(companyId);

    String code = normalizeCode(request.getCode());

    validateCode(null, code, departments);

    validateParent(
      null,
      request.getParentId(),
      toMap(departments),
      true
    );

    Department department = new Department();
    department.setCompanyId(companyId);

    applyValues(department, request, code);

    try {
      adminDepartmentRepository.insert(department);
    } catch (DuplicateKeyException ex) {
      throw duplicateCode();
    }

    return new AdminDepartmentCreatedVO(
      department.getId().toString()
    );
  }

  @Override
  @Transactional
  public void update(
    Long departmentId,
    AdminDepartmentSaveRequest request
  ) {
    List<Department> departments =
      loadCompanyDepartmentsForUpdate(departmentId);

    Map<Long, Department> departmentMap = toMap(departments);
    Department current = requireDepartment(departmentId, departmentMap);

    if (
      request.getCompanyId() != null
        && !Objects.equals(
        request.getCompanyId(),
        current.getCompanyId()
      )
    ) {
      throw new BusinessException(
        ErrorCode.BAD_REQUEST,
        "Department company cannot be changed"
      );
    }

    String code = normalizeCode(request.getCode());

    validateCode(departmentId, code, departments);

    validateParent(
      departmentId,
      request.getParentId(),
      departmentMap,
      true
    );

    applyValues(current, request, code);

    try {
      adminDepartmentRepository.update(current);
    } catch (DuplicateKeyException ex) {
      throw duplicateCode();
    }
  }

  @Override
  @Transactional
  public void updateStatus(
    Long departmentId,
    AdminDepartmentStatusRequest request
  ) {
    List<Department> departments =
      loadCompanyDepartmentsForUpdate(departmentId);

    Map<Long, Department> departmentMap = toMap(departments);
    Department current = requireDepartment(departmentId, departmentMap);

    int status = request.getStatus();

    if (status != 0 && status != 1) {
      throw new BusinessException(ErrorCode.BAD_REQUEST);
    }

    if (Objects.equals(current.getStatus(), status)) {
      return;
    }

    if (status == 1) {
      validateParent(
        departmentId,
        current.getParentId(),
        departmentMap,
        true
      );
    } else {
      validateCanDisable(current, departments, departmentMap);
    }

    adminDepartmentRepository.updateStatus(
      departmentId,
      current.getCompanyId(),
      status
    );
  }

  private List<Department> loadCompanyDepartmentsForUpdate(
    Long departmentId
  ) {
    if (departmentId == null || departmentId <= 0) {
      throw new BusinessException(ErrorCode.BAD_REQUEST);
    }

    Department department =
      adminDepartmentRepository.findById(departmentId);

    if (department == null) {
      throw departmentNotFound();
    }

    Long companyId =
      companyAccessService.requireCompanyId(department.getCompanyId());

    lockActiveCompany(companyId);

    return adminDepartmentRepository.findByCompanyIdForUpdate(
      companyId
    );
  }

  private void lockActiveCompany(Long companyId) {
    // Serialize organization changes within the same company.
    Company company =
      adminCompanyRepository.findByIdForUpdate(companyId);

    if (company == null) {
      throw new BusinessException(ErrorCode.COMPANY_NOT_FOUND);
    }

    if (!Integer.valueOf(1).equals(company.getStatus())) {
      throw new BusinessException(ErrorCode.COMPANY_DISABLED);
    }
  }

  private void validateCode(
    Long currentId,
    String code,
    List<Department> departments
  ) {
    boolean exists = departments.stream().anyMatch(
      department ->
        !Objects.equals(department.getId(), currentId)
          && code.equalsIgnoreCase(department.getCode())
    );

    if (exists) {
      throw duplicateCode();
    }
  }

  private void validateParent(
    Long currentId,
    Long parentId,
    Map<Long, Department> departments,
    boolean requireActive
  ) {
    Set<Long> visited = new HashSet<>();
    Long cursor = parentId;

    while (cursor != null) {
      if (
        Objects.equals(cursor, currentId)
          || !visited.add(cursor)
      ) {
        throw new BusinessException(
          ErrorCode.DATA_CONFLICT,
          "Department hierarchy cannot contain a cycle"
        );
      }

      Department parent = departments.get(cursor);

      if (parent == null) {
        throw new BusinessException(
          ErrorCode.ORGANIZATION_COMPANY_MISMATCH,
          "Parent department must belong to the same company"
        );
      }

      if (
        requireActive
          && !Integer.valueOf(1).equals(parent.getStatus())
      ) {
        throw new BusinessException(
          ErrorCode.DATA_CONFLICT,
          "Parent departments must be active"
        );
      }

      cursor = parent.getParentId();
    }
  }

  private void validateCanDisable(
    Department current,
    List<Department> departments,
    Map<Long, Department> departmentMap
  ) {
    for (Department department : departments) {
      if (
        !Objects.equals(department.getId(), current.getId())
          && Integer.valueOf(1).equals(department.getStatus())
          && isDescendantOf(
          department,
          current.getId(),
          departmentMap
        )
      ) {
        throw new BusinessException(
          ErrorCode.ORGANIZATION_UNIT_IN_USE,
          "Disable active child departments first"
        );
      }
    }

    if (
      adminDepartmentRepository.hasActiveUsers(
        current.getId(),
        current.getCompanyId()
      )
    ) {
      throw new BusinessException(
        ErrorCode.ORGANIZATION_UNIT_IN_USE,
        "Reassign active employees before disabling the department"
      );
    }
  }

  private boolean isDescendantOf(
    Department department,
    Long ancestorId,
    Map<Long, Department> departments
  ) {
    Set<Long> visited = new HashSet<>();
    Long cursor = department.getParentId();

    while (cursor != null) {
      if (!visited.add(cursor)) {
        throw new BusinessException(
          ErrorCode.DATA_CONFLICT,
          "Department hierarchy cannot contain a cycle"
        );
      }

      if (Objects.equals(cursor, ancestorId)) {
        return true;
      }

      Department parent = departments.get(cursor);

      if (parent == null) {
        throw new BusinessException(
          ErrorCode.ORGANIZATION_COMPANY_MISMATCH
        );
      }

      cursor = parent.getParentId();
    }

    return false;
  }

  private Map<Long, Department> toMap(
    List<Department> departments
  ) {
    Map<Long, Department> result = new LinkedHashMap<>();

    for (Department department : departments) {
      result.put(department.getId(), department);
    }

    return result;
  }

  private Department requireDepartment(
    Long departmentId,
    Map<Long, Department> departments
  ) {
    Department department = departments.get(departmentId);

    if (department == null) {
      throw departmentNotFound();
    }

    return department;
  }

  private void applyValues(
    Department department,
    AdminDepartmentSaveRequest request,
    String code
  ) {
    department.setParentId(request.getParentId());
    department.setName(request.getName().strip());
    department.setCode(code);
    department.setSortOrder(request.getSortOrder());
  }

  private String normalizeCode(String code) {
    return code.strip().toUpperCase(Locale.ROOT);
  }

  private AdminDepartmentTreeVO toTreeVO(
    Department department
  ) {
    AdminDepartmentTreeVO vo = new AdminDepartmentTreeVO();

    vo.setId(department.getId().toString());
    vo.setCompanyId(department.getCompanyId().toString());

    vo.setParentId(
      department.getParentId() == null
        ? null
        : department.getParentId().toString()
    );

    vo.setName(department.getName());
    vo.setCode(department.getCode());
    vo.setSortOrder(department.getSortOrder());
    vo.setStatus(department.getStatus());

    return vo;
  }

  private BusinessException duplicateCode() {
    return new BusinessException(
      ErrorCode.DATA_CONFLICT,
      "Department code already exists in this company"
    );
  }

  private BusinessException departmentNotFound() {
    return new BusinessException(ErrorCode.DEPARTMENT_NOT_FOUND);
  }
}
