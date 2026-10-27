package com.hikari.rent.user.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hikari.rent.auth.repository.AdminRoleRepository;
import com.hikari.rent.auth.repository.model.AdminRoleRow;
import com.hikari.rent.auth.service.CurrentUserService;
import com.hikari.rent.common.enums.ErrorCode;
import com.hikari.rent.common.exception.BusinessException;
import com.hikari.rent.common.result.PageResult;
import com.hikari.rent.company.entity.Company;
import com.hikari.rent.company.repository.AdminCompanyRepository;
import com.hikari.rent.user.dto.AdminUserPageRequest;
import com.hikari.rent.user.dto.AdminUserStatusRequest;
import com.hikari.rent.user.repository.AdminUserRepository;
import com.hikari.rent.user.repository.model.AdminUserRoleRow;
import com.hikari.rent.user.repository.model.AdminUserRow;
import com.hikari.rent.user.service.AdminUserService;
import com.hikari.rent.user.vo.AdminUserVO;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@PreAuthorize(
  "hasRole('PLATFORM_ADMIN') "
    + "and hasAuthority('user:manage')"
)
public class AdminUserServiceImpl implements AdminUserService {

  @Resource
  private AdminUserRepository adminUserRepository;

  @Resource
  private AdminRoleRepository adminRoleRepository;

  @Resource
  private AdminCompanyRepository adminCompanyRepository;

  @Resource
  private CurrentUserService currentUserService;

  @Override
  @Transactional(readOnly = true)
  public PageResult<AdminUserVO> searchPage(
    AdminUserPageRequest request
  ) {
    IPage<AdminUserRow> page =
      adminUserRepository.searchPage(request);

    List<Long> userIds = page.getRecords()
      .stream()
      .map(AdminUserRow::getId)
      .toList();

    Map<Long, List<String>> rolesByUser =
      adminUserRepository.findRolesByUserIds(userIds)
        .stream()
        .collect(Collectors.groupingBy(
          AdminUserRoleRow::getUserId,
          Collectors.mapping(
            AdminUserRoleRow::getCode,
            Collectors.toList()
          )
        ));

    List<AdminUserVO> records = page.getRecords()
      .stream()
      .map(user -> toVO(
        user,
        rolesByUser.getOrDefault(user.getId(), List.of())
      ))
      .toList();

    return new PageResult<>(
      page.getCurrent(),
      page.getSize(),
      page.getTotal(),
      records
    );
  }

  @Override
  @Transactional(readOnly = true)
  public AdminUserVO getDetail(Long userId) {
    validateUserId(userId);

    AdminUserRow user = adminUserRepository.findById(userId);

    if (user == null) {
      throw new BusinessException(ErrorCode.USER_NOT_FOUND);
    }

    List<String> roles =
      adminUserRepository.findRolesByUserIds(List.of(userId))
        .stream()
        .map(AdminUserRoleRow::getCode)
        .toList();

    return toVO(user, roles);
  }

  @Override
  @Transactional
  public void updateStatus(
    Long userId,
    AdminUserStatusRequest request
  ) {
    validateUserId(userId);
    validateStatusRequest(request);

    Long operatorId = currentUserService.requireUserId();

    if (
      request.getStatus() == 0
        && Objects.equals(userId, operatorId)
    ) {
      throw new BusinessException(
        ErrorCode.ACCESS_DENIED,
        "You cannot disable your own account"
      );
    }

    AdminUserRow initial = adminUserRepository.findById(userId);

    if (initial == null) {
      throw new BusinessException(ErrorCode.USER_NOT_FOUND);
    }

    Company company = null;

    // Keep the same company-first lock order as role assignment.
    if (initial.getCompanyId() != null) {
      company = adminCompanyRepository.findByIdForUpdate(
        initial.getCompanyId()
      );

      if (company == null) {
        throw new BusinessException(ErrorCode.COMPANY_NOT_FOUND);
      }
    }

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

    // Recheck the operator after waiting for the authorization lock.
    if (!activeAdminIds.contains(operatorId)) {
      throw new BusinessException(ErrorCode.ACCESS_DENIED);
    }

    AdminUserRow current =
      adminUserRepository.findByIdForUpdate(userId);

    if (current == null) {
      throw new BusinessException(ErrorCode.USER_NOT_FOUND);
    }

    if (
      !Objects.equals(
        initial.getCompanyId(),
        current.getCompanyId()
      )
    ) {
      throw new BusinessException(
        ErrorCode.DATA_CONFLICT,
        "User company changed; refresh and try again"
      );
    }

    if (
      Objects.equals(
        current.getStatus(),
        request.getStatus()
      )
    ) {
      return;
    }

    if (request.getStatus() == 0) {
      boolean lastPlatformAdmin =
        activeAdminIds.contains(userId)
          && activeAdminIds.size() == 1;

      if (lastPlatformAdmin) {
        throw new BusinessException(
          ErrorCode.LAST_ADMIN_OPERATION_NOT_ALLOWED
        );
      }
    } else if (
      company != null
        && !Integer.valueOf(1).equals(company.getStatus())
    ) {
      throw new BusinessException(
        ErrorCode.COMPANY_DISABLED,
        "Enable the company before enabling its employees"
      );
    }

    adminUserRepository.updateStatus(
      userId,
      request.getStatus()
    );

    adminUserRepository.enqueueRentalSyncTasks(userId);
  }

  private void validateUserId(Long userId) {
    if (userId == null || userId <= 0) {
      throw new BusinessException(ErrorCode.BAD_REQUEST);
    }
  }

  private void validateStatusRequest(
    AdminUserStatusRequest request
  ) {
    if (
      request == null
        || request.getStatus() == null
        || (request.getStatus() != 0 && request.getStatus() != 1)
    ) {
      throw new BusinessException(ErrorCode.BAD_REQUEST);
    }
  }

  private AdminUserVO toVO(
    AdminUserRow user,
    List<String> roles
  ) {
    return new AdminUserVO(
      user.getId().toString(),
      user.getEmail(),
      user.getNickname(),
      user.getPhone(),
      toId(user.getCompanyId()),
      user.getCompanyName(),
      toId(user.getDepartmentId()),
      user.getDepartmentName(),
      toId(user.getPositionId()),
      user.getPositionName(),
      user.getStatus(),
      List.copyOf(roles),
      user.getLastLoginAt(),
      user.getCreatedAt(),
      user.getUpdatedAt()
    );
  }

  private String toId(Long id) {
    return id == null ? null : id.toString();
  }
}
