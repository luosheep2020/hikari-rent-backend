package com.hikari.rent.company.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hikari.rent.common.enums.ErrorCode;
import com.hikari.rent.common.exception.BusinessException;
import com.hikari.rent.common.result.PageResult;
import com.hikari.rent.company.dto.AdminCompanyPageRequest;
import com.hikari.rent.company.dto.AdminCompanySaveRequest;
import com.hikari.rent.company.dto.AdminCompanyStatusRequest;
import com.hikari.rent.company.entity.Company;
import com.hikari.rent.company.repository.AdminCompanyRepository;
import com.hikari.rent.company.service.AdminCompanyService;
import com.hikari.rent.company.vo.AdminCompanyCreatedVO;
import com.hikari.rent.company.vo.AdminCompanyVO;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Objects;

@Service
@PreAuthorize("hasRole('PLATFORM_ADMIN')")
public class AdminCompanyServiceImpl implements AdminCompanyService {

  @Resource
  private AdminCompanyRepository adminCompanyRepository;

  @Override
  @Transactional(readOnly = true)
  public PageResult<AdminCompanyVO> searchPage(
    AdminCompanyPageRequest request
  ) {
    IPage<Company> page =
      adminCompanyRepository.searchPage(request);

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
  public AdminCompanyVO getDetail(Long companyId) {
    validateId(companyId);

    Company company =
      adminCompanyRepository.findById(companyId);

    if (company == null) {
      throw companyNotFound();
    }

    return toVO(company);
  }

  @Override
  @Transactional
  public AdminCompanyCreatedVO create(
    AdminCompanySaveRequest request
  ) {
    Company company = new Company();

    applyValues(company, request);

    adminCompanyRepository.insert(company);

    return new AdminCompanyCreatedVO(
      company.getId().toString()
    );
  }

  @Override
  @Transactional
  public void update(
    Long companyId,
    AdminCompanySaveRequest request
  ) {
    Company company = requireCompanyForUpdate(companyId);

    applyValues(company, request);

    adminCompanyRepository.update(company);

    // Company information can appear in rental search documents.
    adminCompanyRepository.enqueueRelatedRentalSyncTasks(
      companyId
    );
  }

  @Override
  @Transactional
  public void updateStatus(
    Long companyId,
    AdminCompanyStatusRequest request
  ) {
    if (
      request == null
        || request.getStatus() == null
        || (request.getStatus() != 0 && request.getStatus() != 1)
    ) {
      throw new BusinessException(ErrorCode.BAD_REQUEST);
    }

    Company company = requireCompanyForUpdate(companyId);

    // Repeating the same state does not enqueue duplicate work.
    if (Objects.equals(company.getStatus(), request.getStatus())) {
      return;
    }

    adminCompanyRepository.updateStatus(
      companyId,
      request.getStatus()
    );

    adminCompanyRepository.enqueueRelatedRentalSyncTasks(
      companyId
    );
  }

  private Company requireCompanyForUpdate(Long companyId) {
    validateId(companyId);

    Company company =
      adminCompanyRepository.findByIdForUpdate(companyId);

    if (company == null) {
      throw companyNotFound();
    }

    return company;
  }

  private void applyValues(
    Company company,
    AdminCompanySaveRequest request
  ) {
    company.setName(request.getName().strip());
    company.setPostalCode(normalize(request.getPostalCode()));
    company.setAddress(request.getAddress().strip());
    company.setPhone(normalize(request.getPhone()));

    String email = normalize(request.getEmail());

    company.setEmail(
      email == null ? null : email.toLowerCase(Locale.ROOT)
    );

    company.setLicenseNumber(
      normalize(request.getLicenseNumber())
    );

    company.setBusinessHours(
      normalize(request.getBusinessHours())
    );

    company.setClosedDays(
      normalize(request.getClosedDays())
    );
  }

  private AdminCompanyVO toVO(Company company) {
    return new AdminCompanyVO(
      company.getId().toString(),
      company.getName(),
      company.getPostalCode(),
      company.getAddress(),
      company.getPhone(),
      company.getEmail(),
      company.getLicenseNumber(),
      company.getBusinessHours(),
      company.getClosedDays(),
      company.getStatus(),
      company.getCreatedAt(),
      company.getUpdatedAt()
    );
  }

  private String normalize(String value) {
    if (value == null) {
      return null;
    }

    String normalized = value.strip();

    return normalized.isBlank() ? null : normalized;
  }

  private void validateId(Long companyId) {
    if (companyId == null || companyId <= 0) {
      throw new BusinessException(ErrorCode.BAD_REQUEST);
    }
  }

  private BusinessException companyNotFound() {
    return new BusinessException(ErrorCode.COMPANY_NOT_FOUND);
  }
}
