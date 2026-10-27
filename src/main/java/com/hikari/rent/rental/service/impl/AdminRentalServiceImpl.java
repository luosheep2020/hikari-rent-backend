package com.hikari.rent.rental.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hikari.rent.common.enums.ErrorCode;
import com.hikari.rent.common.exception.BusinessException;
import com.hikari.rent.common.result.PageResult;
import com.hikari.rent.file.service.FileUrlService;
import com.hikari.rent.rental.dto.admin.AdminRentalCreateRequest;
import com.hikari.rent.rental.dto.admin.AdminRentalPageRequest;
import com.hikari.rent.rental.dto.admin.AdminRentalSubmitReviewRequest;
import com.hikari.rent.rental.dto.admin.AdminRentalUpdateRequest;
import com.hikari.rent.rental.model.RentalAccessScope;
import com.hikari.rent.rental.repository.AdminRentalRepository;
import com.hikari.rent.rental.repository.RentalRepository;
import com.hikari.rent.rental.repository.model.AdminRentalInsertRow;
import com.hikari.rent.rental.repository.model.AdminRentalReviewRow;
import com.hikari.rent.rental.repository.model.AdminRentalUpdateRow;
import com.hikari.rent.rental.search.service.RentalIndexSyncTaskService;
import com.hikari.rent.rental.service.AdminRentalService;
import com.hikari.rent.rental.service.RentalAccessService;
import com.hikari.rent.rental.vo.admin.AdminRentalCreatedVO;
import com.hikari.rent.rental.vo.admin.AdminRentalDetailVO;
import com.hikari.rent.rental.vo.admin.AdminRentalListVO;
import com.hikari.rent.rental.vo.admin.AdminRentalReviewSubmittedVO;
import com.hikari.rent.rental.vo.admin.AdminRentalUpdatedVO;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class AdminRentalServiceImpl implements AdminRentalService {

  private static final int MAX_PHOTOS = 20;
  private static final int MAX_FLOOR_PLANS = 1;
  private static final int MAX_IMAGES = MAX_PHOTOS + MAX_FLOOR_PLANS;

  @Resource
  private RentalAccessService rentalAccessService;

  @Resource
  private AdminRentalRepository adminRentalRepository;

  @Resource
  private RentalRepository rentalRepository;

  @Resource
  private FileUrlService fileUrlService;

  @Resource
  private RentalIndexSyncTaskService rentalIndexSyncTaskService;

  @Override
  @Transactional(readOnly = true)
  public PageResult<AdminRentalListVO> searchPage(
    AdminRentalPageRequest request
  ) {
    RentalAccessScope scope =
      rentalAccessService.requireManagementScope();

    IPage<AdminRentalListVO> page =
      adminRentalRepository.searchPage(request, scope);

    return new PageResult<>(
      page.getCurrent(),
      page.getSize(),
      page.getTotal(),
      page.getRecords()
    );
  }

  @Override
  @Transactional(readOnly = true)
  public AdminRentalDetailVO getDetail(Long rentalId) {
    validateRentalId(rentalId);

    RentalAccessScope scope =
      rentalAccessService.requireManagementScope();

    AdminRentalDetailVO detail =
      adminRentalRepository.findDetail(rentalId, scope);

    if (detail == null) {
      throw new BusinessException(ErrorCode.RENTAL_NOT_FOUND);
    }

    List<Long> rentalIds = List.of(rentalId);

    detail.setImages(
      rentalRepository.findImagesByRentalIds(rentalIds)
        .stream()
        .map(image -> new AdminRentalDetailVO.Image(
          image.getFileId().toString(),
          image.getKind(),
          fileUrlService.getPublicUrl(image.getStorageKey()),
          image.getSortOrder()
        ))
        .toList()
    );

    detail.setStations(
      rentalRepository.findStationsByRentalIds(rentalIds)
        .stream()
        .map(station -> new AdminRentalDetailVO.Station(
          station.getStationId().toString(),
          station.getName(),
          station.getLineName(),
          station.getWalkingMinutes()
        ))
        .toList()
    );

    detail.setFeatures(
      rentalRepository.findFeaturesByRentalIds(rentalIds)
        .stream()
        .map(feature -> new AdminRentalDetailVO.Feature(
          feature.getCode(),
          feature.getName(),
          feature.getCategory()
        ))
        .toList()
    );

    return detail;
  }

  @Override
  @Transactional
  @PreAuthorize("hasAuthority('rental:create')")
  public AdminRentalCreatedVO createDraft(
    AdminRentalCreateRequest request
  ) {
    RentalAccessScope scope =
      rentalAccessService.requireManagementScope();

    Long userId = scope.userId();

    Long companyId = resolveCreateCompanyId(
      scope,
      request.getCompanyId()
    );

    Long agentId = resolveAgentId(
      scope,
      request.getAgentId(),
      null
    );

    validateDraft(request, companyId, agentId);
    normalizeDraft(request);

    AdminRentalInsertRow rental = new AdminRentalInsertRow();
    rental.setCompanyId(companyId);
    rental.setAgentId(agentId);
    rental.setOperatorId(userId);
    rental.setValues(request);

    adminRentalRepository.insertDraft(rental);

    Long rentalId = rental.getId();

    if (rentalId == null) {
      throw new IllegalStateException(
        "Rental draft insert did not return a generated ID"
      );
    }

    replaceImageRelations(
      rentalId,
      userId,
      request.getImages()
    );

    insertStationAndFeatureRelations(rentalId, request);

    return new AdminRentalCreatedVO(rentalId.toString());
  }

  @Override
  @Transactional
  @PreAuthorize("hasAuthority('rental:update')")
  public AdminRentalUpdatedVO updateDraft(
    Long rentalId,
    AdminRentalUpdateRequest request
  ) {
    validateRentalId(rentalId);
    validateVersion(request.getVersion());

    RentalAccessScope scope =
      rentalAccessService.requireManagementScope();

    AdminRentalDetailVO current =
      adminRentalRepository.findDetail(rentalId, scope);

    if (current == null) {
      throw new BusinessException(ErrorCode.RENTAL_NOT_FOUND);
    }

    validateEditableState(
      current.getStatus(),
      current.getReviewStatus()
    );

    if (!Objects.equals(current.getVersion(), request.getVersion())) {
      throw versionConflict();
    }

    Long companyId = Long.valueOf(current.getCompanyId());

    // Company ownership cannot be changed through a draft update.
    if (
      request.getCompanyId() != null
        && !Objects.equals(request.getCompanyId(), companyId)
    ) {
      throw new BusinessException(ErrorCode.BAD_REQUEST);
    }

    Long agentId = resolveAgentId(
      scope,
      request.getAgentId(),
      Long.valueOf(current.getAgentId())
    );

    validateDraft(request, companyId, agentId);
    normalizeDraft(request);

    AdminRentalUpdateRow rental = new AdminRentalUpdateRow();
    rental.setId(rentalId);
    rental.setCompanyId(companyId);
    rental.setAgentId(agentId);
    rental.setOperatorId(scope.userId());
    rental.setValues(request);

    // The SQL also checks scope, version, and editable states.
    boolean updated = adminRentalRepository.updateDraft(
      rental,
      scope
    );

    if (!updated) {
      throw versionConflict();
    }

    replaceImageRelations(
      rentalId,
      scope.userId(),
      request.getImages()
    );

    adminRentalRepository.deleteStationRelations(rentalId);
    adminRentalRepository.deleteFeatureRelations(rentalId);

    insertStationAndFeatureRelations(rentalId, request);

    rentalIndexSyncTaskService.requestSync(rentalId);

    return new AdminRentalUpdatedVO(
      rentalId.toString(),
      request.getVersion() + 1
    );
  }

  @Override
  @Transactional
  @PreAuthorize("hasAuthority('rental:submit')")
  public AdminRentalReviewSubmittedVO submitReview(
    Long rentalId,
    AdminRentalSubmitReviewRequest request
  ) {
    validateRentalId(rentalId);
    validateVersion(request.getVersion());

    RentalAccessScope scope =
      rentalAccessService.requireManagementScope();

    AdminRentalReviewRow current =
      adminRentalRepository.findReviewTargetForUpdate(
        rentalId,
        scope
      );

    if (current == null) {
      throw new BusinessException(ErrorCode.RENTAL_NOT_FOUND);
    }

    validateEditableState(
      current.getStatus(),
      current.getReviewStatus()
    );

    if (!Objects.equals(current.getVersion(), request.getVersion())) {
      throw versionConflict();
    }

    boolean updated = adminRentalRepository.submitReview(
      rentalId,
      scope,
      request.getVersion()
    );

    if (!updated) {
      throw versionConflict();
    }

    rentalIndexSyncTaskService.requestSync(rentalId);

    return new AdminRentalReviewSubmittedVO(
      rentalId.toString(),
      request.getVersion() + 1,
      1
    );
  }

  private Long resolveCreateCompanyId(
    RentalAccessScope scope,
    Long requestedCompanyId
  ) {
    if (scope.isPlatform()) {
      if (requestedCompanyId == null || requestedCompanyId <= 0) {
        throw new BusinessException(ErrorCode.BAD_REQUEST);
      }

      return requestedCompanyId;
    }

    if (
      requestedCompanyId != null
        && !Objects.equals(requestedCompanyId, scope.companyId())
    ) {
      throw forbidden();
    }

    return scope.companyId();
  }

  private Long resolveAgentId(
    RentalAccessScope scope,
    Long requestedAgentId,
    Long existingAgentId
  ) {
    if (scope.isSelf()) {
      if (
        requestedAgentId != null
          && !Objects.equals(requestedAgentId, scope.userId())
      ) {
        throw forbidden();
      }

      return scope.userId();
    }

    if (requestedAgentId != null) {
      return requestedAgentId;
    }

    // Preserve the current agent when an administrator omits the field.
    if (existingAgentId != null) {
      return existingAgentId;
    }

    throw new BusinessException(ErrorCode.BAD_REQUEST);
  }

  private void validateDraft(
    AdminRentalCreateRequest request,
    Long companyId,
    Long agentId
  ) {
    if (
      companyId == null
        || !adminRentalRepository.activeCompanyExists(companyId)
    ) {
      throw invalidDraft();
    }

    if (
      !adminRentalRepository.municipalityExists(
        request.getAreaCode()
      )
    ) {
      throw invalidDraft();
    }

    if (
      agentId == null
        || !adminRentalRepository.activeEmployeeExists(
        agentId,
        companyId
      )
    ) {
      throw invalidDraft();
    }

    if (
      request.getManagementCompanyId() != null
        && !adminRentalRepository.activeCompanyExists(
        request.getManagementCompanyId()
      )
    ) {
      throw invalidDraft();
    }

    if (
      request.getFloor() != null
        && request.getTotalFloors() != null
        && request.getFloor() > request.getTotalFloors()
    ) {
      throw invalidDraft();
    }

    if (
      request.getBuiltMonth() != null
        && request.getBuiltYear() == null
    ) {
      throw invalidDraft();
    }

    boolean hasLatitude = request.getLatitude() != null;
    boolean hasLongitude = request.getLongitude() != null;

    if (hasLatitude != hasLongitude) {
      throw invalidDraft();
    }

    if (
      request.getStations() == null
        || request.getFeatureCodes() == null
    ) {
      throw invalidDraft();
    }

    if (request.getStations().stream().anyMatch(Objects::isNull)) {
      throw invalidDraft();
    }

    List<Long> stationIds = request.getStations()
      .stream()
      .map(AdminRentalCreateRequest.Station::stationId)
      .toList();

    if (
      stationIds.stream().anyMatch(Objects::isNull)
        || new HashSet<>(stationIds).size() != stationIds.size()
    ) {
      throw invalidDraft();
    }

    if (
      adminRentalRepository.countActiveStations(stationIds)
        != stationIds.size()
    ) {
      throw invalidDraft();
    }

    List<String> featureCodes = request.getFeatureCodes();

    if (
      featureCodes.stream().anyMatch(
        code -> code == null || code.isBlank()
      )
        || new HashSet<>(featureCodes).size() != featureCodes.size()
    ) {
      throw invalidDraft();
    }

    if (
      adminRentalRepository.countActiveFeatures(featureCodes)
        != featureCodes.size()
    ) {
      throw invalidDraft();
    }

    validateImages(request.getImages());
  }

  private void normalizeDraft(AdminRentalCreateRequest request) {
    request.setName(request.getName().trim());
    request.setAddress(request.getAddress().trim());
    request.setFloorPlan(request.getFloorPlan().trim());
  }

  private void insertStationAndFeatureRelations(
    Long rentalId,
    AdminRentalCreateRequest request
  ) {
    int stationCount =
      adminRentalRepository.insertStationRelations(
        rentalId,
        request.getStations()
      );

    int featureCount =
      adminRentalRepository.insertFeatureRelations(
        rentalId,
        request.getFeatureCodes()
      );

    if (
      stationCount != request.getStations().size()
        || featureCount != request.getFeatureCodes().size()
    ) {
      throw invalidDraft();
    }
  }

  private void validateImages(
    List<AdminRentalCreateRequest.Image> images
  ) {
    if (images == null || images.size() > MAX_IMAGES) {
      throw invalidDraft();
    }

    Set<Long> fileIds = new HashSet<>();
    Set<String> imagePositions = new HashSet<>();

    int photoCount = 0;
    int floorPlanCount = 0;

    for (AdminRentalCreateRequest.Image image : images) {
      if (
        image == null
          || image.fileId() == null
          || image.fileId() <= 0
          || image.sortOrder() == null
          || image.sortOrder() < 0
      ) {
        throw invalidDraft();
      }

      if ("PHOTO".equals(image.kind())) {
        photoCount++;
      } else if ("FLOOR_PLAN".equals(image.kind())) {
        floorPlanCount++;
      } else {
        throw invalidDraft();
      }

      if (!fileIds.add(image.fileId())) {
        throw invalidDraft();
      }

      String position = image.kind() + ":" + image.sortOrder();

      if (!imagePositions.add(position)) {
        throw invalidDraft();
      }
    }

    if (
      photoCount > MAX_PHOTOS
        || floorPlanCount > MAX_FLOOR_PLANS
    ) {
      throw invalidDraft();
    }
  }

  private void replaceImageRelations(
    Long rentalId,
    Long userId,
    List<AdminRentalCreateRequest.Image> images
  ) {
    validateImages(images);

    if (!images.isEmpty()) {
      List<Long> fileIds = images.stream()
        .map(AdminRentalCreateRequest.Image::fileId)
        .sorted()
        .toList();

      // Lock file rows before replacing the existing associations.
      List<Long> usableFileIds =
        adminRentalRepository.lockUsableImageFileIds(
          rentalId,
          userId,
          fileIds
        );

      if (
        !new HashSet<>(usableFileIds).equals(
          new HashSet<>(fileIds)
        )
      ) {
        throw invalidDraft();
      }
    }

    adminRentalRepository.deleteImageRelations(rentalId);

    if (images.isEmpty()) {
      return;
    }

    int inserted = adminRentalRepository.insertImageRelations(
      rentalId,
      images
    );

    if (inserted != images.size()) {
      throw invalidDraft();
    }
  }

  private void validateRentalId(Long rentalId) {
    if (rentalId == null || rentalId <= 0) {
      throw new BusinessException(ErrorCode.BAD_REQUEST);
    }
  }

  private void validateVersion(Long version) {
    if (
      version == null
        || version < 0
        || version > 4294967294L
    ) {
      throw new BusinessException(ErrorCode.BAD_REQUEST);
    }
  }

  private void validateEditableState(
    Integer status,
    Integer reviewStatus
  ) {
    boolean editable =
      Integer.valueOf(0).equals(status)
        || Integer.valueOf(3).equals(status);

    boolean underReview =
      Integer.valueOf(1).equals(reviewStatus);

    if (!editable || underReview) {
      throw new BusinessException(
        ErrorCode.RENTAL_EDIT_NOT_ALLOWED
      );
    }
  }

  private BusinessException invalidDraft() {
    return new BusinessException(ErrorCode.RENTAL_DRAFT_INVALID);
  }

  private BusinessException versionConflict() {
    return new BusinessException(
      ErrorCode.RENTAL_VERSION_CONFLICT
    );
  }

  private BusinessException forbidden() {
    return new BusinessException(
      ErrorCode.RENTAL_MANAGEMENT_FORBIDDEN
    );
  }
}
