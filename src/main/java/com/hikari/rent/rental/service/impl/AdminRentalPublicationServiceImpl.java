package com.hikari.rent.rental.service.impl;

import com.hikari.rent.common.enums.ErrorCode;
import com.hikari.rent.common.exception.BusinessException;
import com.hikari.rent.rental.dto.admin.AdminRentalPublicationRequest;
import com.hikari.rent.rental.enums.RentalReviewStatus;
import com.hikari.rent.rental.enums.RentalStatus;
import com.hikari.rent.rental.model.RentalAccessScope;
import com.hikari.rent.rental.repository.AdminRentalRepository;
import com.hikari.rent.rental.repository.model.AdminRentalReviewRow;
import com.hikari.rent.rental.search.service.RentalIndexSyncTaskService;
import com.hikari.rent.rental.service.AdminRentalPublicationService;
import com.hikari.rent.rental.service.RentalAccessService;
import com.hikari.rent.rental.vo.admin.AdminRentalPublicationVO;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class AdminRentalPublicationServiceImpl
  implements AdminRentalPublicationService {

  @Resource
  private RentalAccessService rentalAccessService;

  @Resource
  private AdminRentalRepository adminRentalRepository;

  @Resource
  private RentalIndexSyncTaskService rentalIndexSyncTaskService;

  @Override
  @Transactional
  @PreAuthorize("hasAuthority('rental:publish')")
  public AdminRentalPublicationVO publish(
    Long rentalId,
    AdminRentalPublicationRequest request
  ) {
    return changeStatus(
      rentalId,
      request,
      RentalStatus.PUBLISHED
    );
  }

  @Override
  @Transactional
  @PreAuthorize("hasAuthority('rental:publish')")
  public AdminRentalPublicationVO unpublish(
    Long rentalId,
    AdminRentalPublicationRequest request
  ) {
    return changeStatus(
      rentalId,
      request,
      RentalStatus.ARCHIVED
    );
  }

  @Override
  @Transactional
  @PreAuthorize("hasAuthority('rental:publish')")
  public AdminRentalPublicationVO markRented(
    Long rentalId,
    AdminRentalPublicationRequest request
  ) {
    return changeStatus(
      rentalId,
      request,
      RentalStatus.RENTED
    );
  }

  private AdminRentalPublicationVO changeStatus(
    Long rentalId,
    AdminRentalPublicationRequest request,
    RentalStatus targetStatus
  ) {
    RentalAccessScope scope =
      rentalAccessService.requireManagementScope();

    validateRequest(rentalId, request);

    // Authorize and lock the rental before changing its state.
    AdminRentalReviewRow current =
      adminRentalRepository.findReviewTargetForUpdate(
        rentalId,
        scope
      );

    if (current == null) {
      throw new BusinessException(
        ErrorCode.RENTAL_NOT_FOUND
      );
    }

    if (
      !Objects.equals(
        current.getVersion(),
        request.getVersion()
      )
    ) {
      throw new BusinessException(
        ErrorCode.RENTAL_VERSION_CONFLICT
      );
    }

    boolean updated = switch (targetStatus) {
      case PUBLISHED -> {
        validatePublishState(current);

        if (
          !adminRentalRepository.meetsPublicationRequirements(
            rentalId
          )
        ) {
          throw new BusinessException(
            ErrorCode.RENTAL_PUBLISH_REQUIREMENTS_NOT_MET
          );
        }

        yield adminRentalRepository.publish(
          rentalId,
          scope,
          request.getVersion()
        );
      }

      case ARCHIVED -> {
        validateUnpublishState(current);

        yield adminRentalRepository.unpublish(
          rentalId,
          scope,
          request.getVersion()
        );
      }

      case RENTED -> {
        validateRentedState(current);

        yield adminRentalRepository.markRented(
          rentalId,
          scope,
          request.getVersion()
        );
      }

      default -> throw new BusinessException(
        ErrorCode.RENTAL_STATUS_TRANSITION_NOT_ALLOWED
      );
    };

    if (!updated) {
      throw new BusinessException(
        ErrorCode.RENTAL_UPDATE_CONFLICT
      );
    }

    // Persist the sync task in the same transaction.
    rentalIndexSyncTaskService.requestSync(rentalId);

    return new AdminRentalPublicationVO(
      rentalId.toString(),
      request.getVersion() + 1,
      targetStatus.getCode()
    );
  }

  private void validatePublishState(
    AdminRentalReviewRow current
  ) {
    boolean allowedStatus =
      hasStatus(current, RentalStatus.DRAFT)
        || hasStatus(current, RentalStatus.ARCHIVED);

    if (!allowedStatus) {
      throw invalidTransition();
    }

    if (!isApproved(current)) {
      throw new BusinessException(
        ErrorCode.RENTAL_PUBLISH_REQUIREMENTS_NOT_MET
      );
    }
  }

  private void validateUnpublishState(
    AdminRentalReviewRow current
  ) {
    if (!hasStatus(current, RentalStatus.PUBLISHED)) {
      throw invalidTransition();
    }
  }

  private void validateRentedState(
    AdminRentalReviewRow current
  ) {
    boolean allowedStatus =
      hasStatus(current, RentalStatus.PUBLISHED)
        || hasStatus(current, RentalStatus.ARCHIVED);

    if (!allowedStatus || !isApproved(current)) {
      throw invalidTransition();
    }
  }

  private boolean hasStatus(
    AdminRentalReviewRow current,
    RentalStatus status
  ) {
    return Integer.valueOf(status.getCode())
      .equals(current.getStatus());
  }

  private boolean isApproved(
    AdminRentalReviewRow current
  ) {
    return Integer.valueOf(
      RentalReviewStatus.APPROVED.getCode()
    ).equals(current.getReviewStatus());
  }

  private void validateRequest(
    Long rentalId,
    AdminRentalPublicationRequest request
  ) {
    if (
      rentalId == null
        || rentalId <= 0
        || request == null
        || request.getVersion() == null
        || request.getVersion() < 0
        || request.getVersion() > 2147483646L
    ) {
      throw new BusinessException(
        ErrorCode.BAD_REQUEST
      );
    }
  }

  private BusinessException invalidTransition() {
    return new BusinessException(
      ErrorCode.RENTAL_STATUS_TRANSITION_NOT_ALLOWED
    );
  }
}
