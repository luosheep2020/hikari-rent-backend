package com.hikari.rent.rental.service;

import com.hikari.rent.auth.model.CurrentUserContext;
import com.hikari.rent.auth.service.CurrentUserContextService;
import com.hikari.rent.common.enums.ErrorCode;
import com.hikari.rent.common.exception.BusinessException;
import com.hikari.rent.rental.model.RentalAccessScope;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RentalAccessService {

  @Resource
  private CurrentUserContextService currentUserContextService;

  @Transactional(readOnly = true)
  public RentalAccessScope requireManagementScope() {
    CurrentUserContext user =
      currentUserContextService.requireCurrentUser();

    if (user.hasRole("PLATFORM_ADMIN", "PLATFORM")) {
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

    if (user.hasRole("COMPANY_ADMIN", "COMPANY")) {
      return new RentalAccessScope(
        RentalAccessScope.Type.COMPANY,
        user.userId(),
        user.companyId()
      );
    }

    if (user.hasRole("AGENT", "SELF")) {
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
