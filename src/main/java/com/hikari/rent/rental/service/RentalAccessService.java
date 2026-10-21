package com.hikari.rent.rental.service;

import com.hikari.rent.auth.model.CurrentUserContext;
import com.hikari.rent.auth.service.CurrentUserContextService;
import com.hikari.rent.common.enums.ErrorCode;
import com.hikari.rent.common.exception.BusinessException;
import com.hikari.rent.rental.model.RentalAccessScope;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RentalAccessService {

  @Resource
  private CurrentUserContextService currentUserContextService;

  @Value("${app.rental.roles.admin:ADMIN}")
  private String adminRoleCode;

  @Value("${app.rental.roles.company-admin:COMPANY_ADMIN}")
  private String companyAdminRoleCode;

  @Value("${app.rental.roles.agent:AGENT}")
  private String agentRoleCode;

  @Transactional(readOnly = true)
  public RentalAccessScope requireManagementScope() {
    CurrentUserContext user =
      currentUserContextService.requireCurrentUser();

    if (user.hasRole(adminRoleCode, "PLATFORM")) {
      return new RentalAccessScope(
        RentalAccessScope.Type.PLATFORM,
        user.userId(),
        null
      );
    }

    if (
      user.companyId() == null
        || !user.companyActive()
    ) {
      throw forbidden();
    }

    if (
      user.hasRole(
        companyAdminRoleCode,
        "COMPANY"
      )
    ) {
      return new RentalAccessScope(
        RentalAccessScope.Type.COMPANY,
        user.userId(),
        user.companyId()
      );
    }

    if (user.hasRole(agentRoleCode, "SELF")) {
      return new RentalAccessScope(
        RentalAccessScope.Type.SELF,
        user.userId(),
        user.companyId()
      );
    }

    throw forbidden();
  }

  private BusinessException forbidden() {
    return new BusinessException(
      ErrorCode.RENTAL_MANAGEMENT_FORBIDDEN
    );
  }
}
