package com.hikari.rent.auth.service.impl;

import com.hikari.rent.auth.dto.AdminRolePermissionsRequest;
import com.hikari.rent.auth.repository.AdminPermissionRepository;
import com.hikari.rent.auth.repository.model.AdminPermissionRow;
import com.hikari.rent.auth.repository.model.AdminRoleRow;
import com.hikari.rent.auth.service.AdminPermissionService;
import com.hikari.rent.auth.vo.AdminPermissionTreeVO;
import com.hikari.rent.common.enums.ErrorCode;
import com.hikari.rent.common.exception.BusinessException;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@PreAuthorize("hasRole('PLATFORM_ADMIN')")
public class AdminPermissionServiceImpl
  implements AdminPermissionService {

  @Resource
  private AdminPermissionRepository adminPermissionRepository;

  @Override
  @Transactional(readOnly = true)
  public List<AdminPermissionTreeVO> getTree() {
    List<AdminPermissionRow> permissions =
      adminPermissionRepository.findAllPermissions();

    Map<String, AdminPermissionTreeVO> modules =
      new LinkedHashMap<>();

    for (AdminPermissionRow permission : permissions) {
      AdminPermissionTreeVO moduleNode =
        modules.computeIfAbsent(
          permission.getModule(),
          this::createModuleNode
        );

      moduleNode.getChildren().add(
        createPermissionNode(permission)
      );
    }

    return new ArrayList<>(modules.values());
  }

  @Override
  @Transactional(readOnly = true)
  public List<String> getRolePermissionIds(Long roleId) {
    validateRoleId(roleId);

    AdminRoleRow role =
      adminPermissionRepository.findRoleById(roleId);

    if (role == null) {
      throw new BusinessException(ErrorCode.ROLE_NOT_FOUND);
    }

    return adminPermissionRepository
      .findRolePermissionIds(roleId)
      .stream()
      .map(String::valueOf)
      .toList();
  }

  @Override
  @Transactional
  @PreAuthorize(
    "hasRole('PLATFORM_ADMIN') "
      + "and hasAuthority('role:manage')"
  )
  public void updateRolePermissions(
    Long roleId,
    AdminRolePermissionsRequest request
  ) {
    validateRoleId(roleId);

    List<Long> permissionIds = validatePermissionIds(request);

    AdminRoleRow role =
      adminPermissionRepository.findRoleByIdForUpdate(roleId);

    if (role == null) {
      throw new BusinessException(ErrorCode.ROLE_NOT_FOUND);
    }

    if ("PLATFORM_ADMIN".equals(role.getCode())) {
      throw new BusinessException(
        ErrorCode.PROTECTED_ROLE_OPERATION_NOT_ALLOWED,
        "Platform administrator permissions are protected"
      );
    }

    if (!Integer.valueOf(1).equals(role.getStatus())) {
      throw new BusinessException(
        ErrorCode.DATA_CONFLICT,
        "Enable the role before changing its permissions"
      );
    }

    Set<Long> availablePermissionIds =
      adminPermissionRepository.findAllPermissionsForShare()
        .stream()
        .map(AdminPermissionRow::getId)
        .collect(Collectors.toSet());

    if (!availablePermissionIds.containsAll(permissionIds)) {
      throw new BusinessException(
        ErrorCode.PERMISSION_NOT_FOUND
      );
    }

    adminPermissionRepository.deleteRolePermissions(roleId);

    if (permissionIds.isEmpty()) {
      return;
    }

    int inserted =
      adminPermissionRepository.insertRolePermissions(
        roleId,
        permissionIds
      );

    if (inserted != permissionIds.size()) {
      throw new IllegalStateException(
        "Not all requested permissions were assigned"
      );
    }
  }

  private List<Long> validatePermissionIds(
    AdminRolePermissionsRequest request
  ) {
    if (
      request == null
        || request.getPermissionIds() == null
        || request.getPermissionIds().size() > 500
    ) {
      throw new BusinessException(ErrorCode.BAD_REQUEST);
    }

    List<Long> permissionIds = request.getPermissionIds();

    if (
      permissionIds.stream().anyMatch(
        id -> id == null || id <= 0
      )
        || new HashSet<>(permissionIds).size()
        != permissionIds.size()
    ) {
      throw new BusinessException(
        ErrorCode.BAD_REQUEST,
        "Permission IDs must be positive and unique"
      );
    }

    return permissionIds.stream().sorted().toList();
  }

  private void validateRoleId(Long roleId) {
    if (roleId == null || roleId <= 0) {
      throw new BusinessException(ErrorCode.BAD_REQUEST);
    }
  }

  private AdminPermissionTreeVO createModuleNode(
    String module
  ) {
    AdminPermissionTreeVO node =
      new AdminPermissionTreeVO();

    node.setKey("module:" + module);
    node.setTitle(module);
    node.setModule(module);

    return node;
  }

  private AdminPermissionTreeVO createPermissionNode(
    AdminPermissionRow permission
  ) {
    AdminPermissionTreeVO node =
      new AdminPermissionTreeVO();

    node.setKey("permission:" + permission.getId());
    node.setTitle(permission.getName());
    node.setModule(permission.getModule());
    node.setPermissionId(permission.getId().toString());
    node.setCode(permission.getCode());
    node.setDescription(permission.getDescription());

    return node;
  }
}
