package com.hikari.rent.company.service;

import com.hikari.rent.auth.model.CurrentUserContext;
import com.hikari.rent.auth.service.CurrentUserContextService;
import com.hikari.rent.common.enums.ErrorCode;
import com.hikari.rent.common.exception.BusinessException;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class CompanyAccessService {

  @Resource
  private CurrentUserContextService currentUserContextService;

  public Long requireCompanyId(Long requestedCompanyId) {
    CurrentUserContext user =
      currentUserContextService.requireCurrentUser();

    if (user.hasRole("PLATFORM_ADMIN", "PLATFORM")) {
      if (
        requestedCompanyId == null
          || requestedCompanyId <= 0
      ) {
        throw new BusinessException(
          ErrorCode.BAD_REQUEST,
          "Company ID is required"
        );
      }

      return requestedCompanyId;
    }

    if (
      !user.hasRole("COMPANY_ADMIN", "COMPANY")
        || user.companyId() == null
        || !user.companyActive()
    ) {
      throw new BusinessException(ErrorCode.ACCESS_DENIED);
    }

    if (
      requestedCompanyId != null
        && !Objects.equals(
        requestedCompanyId,
        user.companyId()
      )
    ) {
      throw new BusinessException(ErrorCode.ACCESS_DENIED);
    }

    return user.companyId();
  }
}
