package com.hikari.rent.auth.service;

import com.hikari.rent.auth.model.CurrentUserContext;
import com.hikari.rent.auth.repository.CurrentUserAccessRepository;
import com.hikari.rent.auth.repository.model.CurrentUserRoleRow;
import com.hikari.rent.common.enums.ErrorCode;
import com.hikari.rent.common.exception.BusinessException;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CurrentUserContextService {

  @Resource
  private CurrentUserService currentUserService;

  @Resource
  private CurrentUserAccessRepository currentUserAccessRepository;

  @Transactional(readOnly = true)
  public CurrentUserContext requireCurrentUser() {
    Long userId = currentUserService.requireUserId();

    var rows = currentUserAccessRepository
      .findUserRoles(userId);

    if (rows.isEmpty()) {
      throw new BusinessException(
        ErrorCode.AUTHENTICATION_REQUIRED
      );
    }

    CurrentUserRoleRow account = rows.getFirst();

    Set<CurrentUserContext.Role> roles = rows.stream()
      .filter(row ->
        row.getRoleCode() != null
          && row.getRoleScope() != null
      )
      .map(row -> new CurrentUserContext.Role(
        row.getRoleCode(),
        row.getRoleScope()
      ))
      .collect(Collectors.toSet());

    return new CurrentUserContext(
      account.getUserId(),
      account.getCompanyId(),
      Integer.valueOf(1).equals(
        account.getCompanyStatus()
      ),
      roles
    );
  }
}
