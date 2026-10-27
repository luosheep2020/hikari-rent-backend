package com.hikari.rent.auth.service.impl;

import com.hikari.rent.auth.dto.AdminUserRolesRequest;
import com.hikari.rent.auth.repository.AdminRoleRepository;
import com.hikari.rent.auth.repository.model.AdminRoleRow;
import com.hikari.rent.auth.repository.model.AdminRoleUserRow;
import com.hikari.rent.auth.service.AdminRoleService;
import com.hikari.rent.auth.service.CurrentUserService;
import com.hikari.rent.auth.vo.AdminRoleVO;
import com.hikari.rent.common.enums.ErrorCode;
import com.hikari.rent.common.exception.BusinessException;
import com.hikari.rent.company.entity.Company;
import com.hikari.rent.company.repository.AdminCompanyRepository;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Service
@PreAuthorize("hasRole('PLATFORM_ADMIN')")
public class AdminRoleServiceImpl implements AdminRoleService {

  @Resource
  private AdminRoleRepository adminRoleRepository;

  @Resource
  private AdminCompanyRepository adminCompanyRepository;

  @Resource
  private CurrentUserService currentUserService;

  @Override
  @Transactional(readOnly = true)
  public List<AdminRoleVO> getRoles() {
    return adminRoleRepository.findAllRoles()
      .stream()
      .map(this::toVO)
      .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public List<AdminRoleVO> getUserRoles(Long userId) {
    validateUserId(userId);

    if (adminRoleRepository.findUser(userId) == null) {
      throw new BusinessException(ErrorCode.USER_NOT_FOUND);
    }

    return adminRoleRepository.findUserRoles(userId)
      .stream()
      .map(this::toVO)
      .toList();
  }

  @Override
  @Transactional
  @PreAuthorize(
    "hasRole('PLATFORM_ADMIN') "
      + "and hasAuthority('role:assign')"
  )
  public void updateUserRoles(
    Long userId,
    AdminUserRolesRequest request
  ) {
    validateUserId(userId);

    List<Long> requestedIds = validateRoleIds(request);

    AdminRoleUserRow initial =
      adminRoleRepository.findUser(userId);

    if (initial == null) {
      throw new BusinessException(ErrorCode.USER_NOT_FOUND);
    }

    Company company = null;

    // Follow the same company-first lock order as employee management.
    if (initial.getCompanyId() != null) {
      company = adminCompanyRepository.findByIdForUpdate(
        initial.getCompanyId()
      );

      if (company == null) {
        throw new BusinessException(ErrorCode.COMPANY_NOT_FOUND);
      }
    }

    // All role-assignment requests serialize on this existing role row.
    AdminRoleRow platformRole =
      adminRoleRepository.lockPlatformRole();

    if (platformRole == null) {
      throw new BusinessException(
        ErrorCode.ROLE_NOT_FOUND,
        "An active PLATFORM_ADMIN role is required"
      );
    }

    List<Long> activeAdminIds =
      adminRoleRepository.findActivePlatformAdminIds(
        platformRole.getId()
      );

    // Recheck current database membership after acquiring the lock.
    Long operatorId = currentUserService.requireUserId();

    if (!activeAdminIds.contains(operatorId)) {
      throw new BusinessException(ErrorCode.ACCESS_DENIED);
    }

    AdminRoleUserRow target =
      adminRoleRepository.findUserForUpdate(userId);

    if (target == null) {
      throw new BusinessException(ErrorCode.USER_NOT_FOUND);
    }

    if (
      !Objects.equals(
        initial.getCompanyId(),
        target.getCompanyId()
      )
    ) {
      throw new BusinessException(
        ErrorCode.DATA_CONFLICT,
        "User company changed; refresh and try again"
      );
    }

    Map<Long, AdminRoleRow> availableRoles = new HashMap<>();

    for (AdminRoleRow role :
      adminRoleRepository.findAllRolesForShare()) {
      availableRoles.put(role.getId(), role);
    }

    List<AdminRoleRow> selectedRoles = new ArrayList<>();

    for (Long roleId : requestedIds) {
      AdminRoleRow role = availableRoles.get(roleId);

      if (
        role == null
          || !Integer.valueOf(1).equals(role.getStatus())
      ) {
        throw new BusinessException(
          ErrorCode.ROLE_NOT_FOUND,
          "Every selected role must exist and be active"
        );
      }

      selectedRoles.add(role);
    }

    validateCombination(target, company, selectedRoles);

    boolean removingLastAdmin =
      activeAdminIds.contains(userId)
        && !requestedIds.contains(platformRole.getId())
        && activeAdminIds.size() == 1;

    if (removingLastAdmin) {
      throw new BusinessException(
        ErrorCode.LAST_ADMIN_OPERATION_NOT_ALLOWED
      );
    }

    adminRoleRepository.deleteUserRoles(userId);

    int inserted = adminRoleRepository.insertUserRoles(
      userId,
      requestedIds
    );

    if (inserted != requestedIds.size()) {
      throw new IllegalStateException(
        "Not all requested roles were assigned"
      );
    }

    adminRoleRepository.enqueueRentalSyncTasks(userId);
  }

  private List<Long> validateRoleIds(
    AdminUserRolesRequest request
  ) {
    if (
      request == null
        || request.getRoleIds() == null
        || request.getRoleIds().isEmpty()
        || request.getRoleIds().size() > 4
    ) {
      throw new BusinessException(ErrorCode.BAD_REQUEST);
    }

    List<Long> roleIds = request.getRoleIds();

    if (
      roleIds.stream().anyMatch(id -> id == null || id <= 0)
        || new HashSet<>(roleIds).size() != roleIds.size()
    ) {
      throw new BusinessException(
        ErrorCode.BAD_REQUEST,
        "Role IDs must be positive and unique"
      );
    }

    return roleIds.stream().sorted().toList();
  }

  private void validateCombination(
    AdminRoleUserRow user,
    Company company,
    List<AdminRoleRow> roles
  ) {
    Set<String> codes = new HashSet<>();

    for (AdminRoleRow role : roles) {
      String expectedScope = switch (role.getCode()) {
        case "USER", "AGENT" -> "SELF";
        case "COMPANY_ADMIN" -> "COMPANY";
        case "PLATFORM_ADMIN" -> "PLATFORM";
        default -> null;
      };

      if (
        expectedScope == null
          || !expectedScope.equals(role.getScope())
      ) {
        throw new BusinessException(
          ErrorCode.BAD_REQUEST,
          "Unsupported role or invalid role scope"
        );
      }

      codes.add(role.getCode());
    }

    if (user.getCompanyId() == null) {
      boolean valid =
        codes.size() == 1
          && (
          codes.contains("USER")
            || codes.contains("PLATFORM_ADMIN")
        );

      if (!valid) {
        throw new BusinessException(
          ErrorCode.BAD_REQUEST,
          "Users without a company require USER or PLATFORM_ADMIN"
        );
      }

      return;
    }

    if (
      company == null
        || !Integer.valueOf(1).equals(company.getStatus())
    ) {
      throw new BusinessException(ErrorCode.COMPANY_DISABLED);
    }

    if (
      codes.contains("USER")
        || codes.contains("PLATFORM_ADMIN")
    ) {
      throw new BusinessException(
        ErrorCode.BAD_REQUEST,
        "Company employees may only receive company roles"
      );
    }
  }

  private void validateUserId(Long userId) {
    if (userId == null || userId <= 0) {
      throw new BusinessException(ErrorCode.BAD_REQUEST);
    }
  }

  private AdminRoleVO toVO(AdminRoleRow role) {
    return new AdminRoleVO(
      role.getId().toString(),
      role.getCode(),
      role.getName(),
      role.getDescription(),
      role.getScope(),
      role.getStatus()
    );
  }
}
