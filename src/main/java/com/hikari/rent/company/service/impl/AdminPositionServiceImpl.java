package com.hikari.rent.company.service.impl;

import com.hikari.rent.common.enums.ErrorCode;
import com.hikari.rent.common.exception.BusinessException;
import com.hikari.rent.company.dto.AdminPositionSaveRequest;
import com.hikari.rent.company.dto.AdminPositionStatusRequest;
import com.hikari.rent.company.entity.Company;
import com.hikari.rent.company.entity.Position;
import com.hikari.rent.company.repository.AdminCompanyRepository;
import com.hikari.rent.company.repository.AdminPositionRepository;
import com.hikari.rent.company.service.AdminPositionService;
import com.hikari.rent.company.service.CompanyAccessService;
import com.hikari.rent.company.vo.AdminPositionCreatedVO;
import com.hikari.rent.company.vo.AdminPositionVO;
import jakarta.annotation.Resource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
@PreAuthorize(
  "hasAnyRole('PLATFORM_ADMIN', 'COMPANY_ADMIN') "
    + "and hasAuthority('position:manage')"
)
public class AdminPositionServiceImpl
  implements AdminPositionService {

  @Resource
  private CompanyAccessService companyAccessService;

  @Resource
  private AdminCompanyRepository adminCompanyRepository;

  @Resource
  private AdminPositionRepository adminPositionRepository;

  @Override
  @Transactional(readOnly = true)
  public List<AdminPositionVO> getList(Long companyId) {
    Long allowedCompanyId =
      companyAccessService.requireCompanyId(companyId);

    if (
      adminCompanyRepository.findById(allowedCompanyId) == null
    ) {
      throw new BusinessException(ErrorCode.COMPANY_NOT_FOUND);
    }

    return adminPositionRepository
      .findByCompanyId(allowedCompanyId)
      .stream()
      .map(this::toVO)
      .toList();
  }

  @Override
  @Transactional
  public AdminPositionCreatedVO create(
    AdminPositionSaveRequest request
  ) {
    Long companyId =
      companyAccessService.requireCompanyId(
        request.getCompanyId()
      );

    lockActiveCompany(companyId);

    Position position = new Position();
    position.setCompanyId(companyId);

    applyValues(position, request);

    try {
      adminPositionRepository.insert(position);
    } catch (DuplicateKeyException ex) {
      throw duplicateCode();
    }

    return new AdminPositionCreatedVO(
      position.getId().toString()
    );
  }

  @Override
  @Transactional
  public void update(
    Long positionId,
    AdminPositionSaveRequest request
  ) {
    Position position = requirePositionForUpdate(positionId);

    if (
      request.getCompanyId() != null
        && !Objects.equals(
        request.getCompanyId(),
        position.getCompanyId()
      )
    ) {
      throw new BusinessException(
        ErrorCode.BAD_REQUEST,
        "Position company cannot be changed"
      );
    }

    applyValues(position, request);

    try {
      adminPositionRepository.update(position);
    } catch (DuplicateKeyException ex) {
      throw duplicateCode();
    }
  }

  @Override
  @Transactional
  public void updateStatus(
    Long positionId,
    AdminPositionStatusRequest request
  ) {
    if (
      request == null
        || request.getStatus() == null
        || (request.getStatus() != 0 && request.getStatus() != 1)
    ) {
      throw new BusinessException(ErrorCode.BAD_REQUEST);
    }

    Position position = requirePositionForUpdate(positionId);

    if (
      Objects.equals(
        position.getStatus(),
        request.getStatus()
      )
    ) {
      return;
    }

    if (
      request.getStatus() == 0
        && adminPositionRepository.hasActiveUsers(
        positionId,
        position.getCompanyId()
      )
    ) {
      throw new BusinessException(
        ErrorCode.ORGANIZATION_UNIT_IN_USE,
        "Reassign active employees before disabling the position"
      );
    }

    adminPositionRepository.updateStatus(
      positionId,
      position.getCompanyId(),
      request.getStatus()
    );
  }

  private Position requirePositionForUpdate(Long positionId) {
    if (positionId == null || positionId <= 0) {
      throw new BusinessException(ErrorCode.BAD_REQUEST);
    }

    Position existing =
      adminPositionRepository.findById(positionId);

    if (existing == null) {
      throw positionNotFound();
    }

    Long companyId =
      companyAccessService.requireCompanyId(
        existing.getCompanyId()
      );

    lockActiveCompany(companyId);

    // Read the current row after acquiring the company lock.
    Position current =
      adminPositionRepository.findByIdForUpdate(
        positionId,
        companyId
      );

    if (current == null) {
      throw positionNotFound();
    }

    return current;
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

  private void applyValues(
    Position position,
    AdminPositionSaveRequest request
  ) {
    position.setName(request.getName().strip());

    position.setCode(
      request.getCode()
        .strip()
        .toUpperCase(Locale.ROOT)
    );

    position.setSortOrder(request.getSortOrder());
  }

  private AdminPositionVO toVO(Position position) {
    return new AdminPositionVO(
      position.getId().toString(),
      position.getCompanyId().toString(),
      position.getName(),
      position.getCode(),
      position.getSortOrder(),
      position.getStatus(),
      position.getCreatedAt(),
      position.getUpdatedAt()
    );
  }

  private BusinessException duplicateCode() {
    return new BusinessException(
      ErrorCode.DATA_CONFLICT,
      "Position code already exists in this company"
    );
  }

  private BusinessException positionNotFound() {
    return new BusinessException(ErrorCode.POSITION_NOT_FOUND);
  }
}
