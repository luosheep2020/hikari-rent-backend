package com.hikari.rent.rental.service.impl;

import com.hikari.rent.common.enums.ErrorCode;
import com.hikari.rent.common.exception.BusinessException;
import com.hikari.rent.rental.dto.admin.AdminRentalReviewRequest;
import com.hikari.rent.rental.enums.RentalReviewStatus;
import com.hikari.rent.rental.model.RentalAccessScope;
import com.hikari.rent.rental.repository.AdminRentalRepository;
import com.hikari.rent.rental.repository.model.AdminRentalReviewRow;
import com.hikari.rent.rental.search.service.RentalIndexSyncTaskService;
import com.hikari.rent.rental.service.AdminRentalReviewService;
import com.hikari.rent.rental.service.RentalAccessService;
import com.hikari.rent.rental.vo.admin.AdminRentalReviewedVO;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class AdminRentalReviewServiceImpl
  implements AdminRentalReviewService {

  @Resource
  private RentalAccessService rentalAccessService;

  @Resource
  private AdminRentalRepository adminRentalRepository;

  @Resource
  private RentalIndexSyncTaskService rentalIndexSyncTaskService;

  @Override
  @Transactional
  @PreAuthorize("hasAuthority('rental:review')")
  public AdminRentalReviewedVO review(
    Long rentalId,
    AdminRentalReviewRequest request
  ) {
    RentalAccessScope scope =
      rentalAccessService.requireManagementScope();

    // Keep authorization effective for calls outside the controller.
    if (!scope.isPlatform()) {
      throw new BusinessException(ErrorCode.ACCESS_DENIED);
    }

    validateRequest(rentalId, request);

    String remark = normalizeRemark(request.getRemark());

    if (!request.getApproved() && remark == null) {
      throw new BusinessException(ErrorCode.VALIDATION_FAILED);
    }

    AdminRentalReviewRow current =
      adminRentalRepository.findReviewTargetForUpdate(
        rentalId,
        scope
      );

    if (current == null) {
      throw new BusinessException(ErrorCode.RENTAL_NOT_FOUND);
    }

    if (!Objects.equals(current.getVersion(), request.getVersion())) {
      throw new BusinessException(
        ErrorCode.RENTAL_VERSION_CONFLICT
      );
    }

    boolean pending = Integer.valueOf(
      RentalReviewStatus.PENDING.getCode()
    ).equals(current.getReviewStatus());

    boolean editableStatus =
      Integer.valueOf(0).equals(current.getStatus())
        || Integer.valueOf(3).equals(current.getStatus());

    if (!pending || !editableStatus) {
      throw new BusinessException(
        ErrorCode.RENTAL_REVIEW_TRANSITION_NOT_ALLOWED
      );
    }

    int reviewStatus = request.getApproved()
      ? RentalReviewStatus.APPROVED.getCode()
      : RentalReviewStatus.REJECTED.getCode();

    boolean updated = adminRentalRepository.review(
      rentalId,
      scope.userId(),
      request.getVersion(),
      reviewStatus,
      remark
    );

    if (!updated) {
      throw new BusinessException(
        ErrorCode.RENTAL_UPDATE_CONFLICT
      );
    }

    // Persist the index task in the same database transaction.
    rentalIndexSyncTaskService.requestSync(rentalId);

    return new AdminRentalReviewedVO(
      rentalId.toString(),
      request.getVersion() + 1,
      reviewStatus
    );
  }

  private void validateRequest(
    Long rentalId,
    AdminRentalReviewRequest request
  ) {
    if (
      rentalId == null
        || rentalId <= 0
        || request == null
        || request.getVersion() == null
        || request.getVersion() < 0
        || request.getVersion() > 2147483646L
        || request.getApproved() == null
    ) {
      throw new BusinessException(ErrorCode.BAD_REQUEST);
    }

    if (
      request.getRemark() != null
        && request.getRemark().length() > 500
    ) {
      throw new BusinessException(ErrorCode.VALIDATION_FAILED);
    }
  }

  private String normalizeRemark(String remark) {
    if (remark == null) {
      return null;
    }

    String normalized = remark.strip();

    return normalized.isBlank() ? null : normalized;
  }
}
