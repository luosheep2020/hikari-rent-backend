package com.hikari.rent.rental.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hikari.rent.auth.service.CurrentUserService;
import com.hikari.rent.common.enums.ErrorCode;
import com.hikari.rent.common.exception.BusinessException;
import com.hikari.rent.common.result.PageResult;
import com.hikari.rent.file.service.FileUrlService;
import com.hikari.rent.rental.dto.admin.AdminRentalCreateRequest;
import com.hikari.rent.rental.dto.admin.AdminRentalPageRequest;
import com.hikari.rent.rental.dto.admin.AdminRentalUpdateRequest;
import com.hikari.rent.rental.repository.AdminRentalRepository;
import com.hikari.rent.rental.repository.RentalRepository;
import com.hikari.rent.rental.repository.model.AdminRentalInsertRow;
import com.hikari.rent.rental.repository.model.AdminRentalUpdateRow;
import com.hikari.rent.rental.service.AdminRentalService;
import com.hikari.rent.rental.vo.admin.AdminRentalCreatedVO;
import com.hikari.rent.rental.vo.admin.AdminRentalDetailVO;
import com.hikari.rent.rental.vo.admin.AdminRentalListVO;
import com.hikari.rent.rental.vo.admin.AdminRentalUpdatedVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@Service
public class AdminRentalServiceImpl
  implements AdminRentalService {

  @Resource
  private CurrentUserService currentUserService;
  @Resource
  private RentalRepository rentalRepository;
  @Resource
  private FileUrlService fileUrlService;
  @Resource
  private AdminRentalRepository adminRentalRepository;

  @Override
  @Transactional(readOnly = true)
  public PageResult<AdminRentalListVO> searchPage(
    AdminRentalPageRequest request
  ) {
    Long userId = currentUserService.requireUserId();

    Long companyId = adminRentalRepository
      .findActiveCompanyId(userId);

    if (companyId == null) {
      throw new BusinessException(
        ErrorCode.RENTAL_MANAGEMENT_FORBIDDEN
      );
    }

    IPage<AdminRentalListVO> page =
      adminRentalRepository.searchPage(
        request,
        userId,
        companyId
      );

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
    if (rentalId == null || rentalId <= 0) {
      throw new BusinessException(
        ErrorCode.BAD_REQUEST
      );
    }

    Long userId = currentUserService.requireUserId();

    Long companyId = adminRentalRepository
      .findActiveCompanyId(userId);

    if (companyId == null) {
      throw new BusinessException(
        ErrorCode.RENTAL_MANAGEMENT_FORBIDDEN
      );
    }

    AdminRentalDetailVO detail =
      adminRentalRepository.findDetail(
        rentalId,
        userId,
        companyId
      );

    if (detail == null) {
      throw new BusinessException(
        ErrorCode.RENTAL_NOT_FOUND
      );
    }

    List<Long> rentalIds = List.of(rentalId);

    detail.setImages(
      rentalRepository.findImagesByRentalIds(rentalIds)
        .stream()
        .map(image -> new AdminRentalDetailVO.Image(
          image.getFileId().toString(),
          image.getKind(),
          fileUrlService.getPublicUrl(
            image.getStorageKey()
          ),
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
  public AdminRentalCreatedVO createDraft(
    AdminRentalCreateRequest request
  ) {
    Long userId = currentUserService.requireUserId();

    Long companyId = adminRentalRepository
      .findActiveCompanyId(userId);

    if (companyId == null) {
      throw new BusinessException(
        ErrorCode.RENTAL_MANAGEMENT_FORBIDDEN
      );
    }

    Long agentId = request.getAgentId() == null
      ? userId
      : request.getAgentId();

    validateDraft(request, companyId, agentId);

    request.setName(request.getName().trim());
    request.setAddress(request.getAddress().trim());
    request.setFloorPlan(request.getFloorPlan().trim());

    AdminRentalInsertRow rental =
      new AdminRentalInsertRow();

    rental.setCompanyId(companyId);
    rental.setAgentId(agentId);
    rental.setOperatorId(userId);
    rental.setValues(request);

    adminRentalRepository.insertDraft(rental);

    Long rentalId = rental.getId();

    int stationCount = adminRentalRepository
      .insertStationRelations(
        rentalId,
        request.getStations()
      );

    int featureCount = adminRentalRepository
      .insertFeatureRelations(
        rentalId,
        request.getFeatureCodes()
      );

    if (
      stationCount != request.getStations().size()
        || featureCount != request.getFeatureCodes().size()
    ) {
      throw invalidDraft();
    }

    return new AdminRentalCreatedVO(
      rentalId.toString()
    );
  }

  private void validateDraft(
    AdminRentalCreateRequest request,
    Long companyId,
    Long agentId
  ) {
    if (
      !adminRentalRepository.municipalityExists(
        request.getAreaCode()
      )
    ) {
      throw invalidDraft();
    }

    if (
      !adminRentalRepository.activeEmployeeExists(
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

    var stationIds = request.getStations()
      .stream()
      .map(AdminRentalCreateRequest.Station::stationId)
      .toList();

    if (
      new HashSet<>(stationIds).size()
        != stationIds.size()
    ) {
      throw invalidDraft();
    }

    if (
      adminRentalRepository.countActiveStations(
        stationIds
      ) != stationIds.size()
    ) {
      throw invalidDraft();
    }

    var featureCodes = request.getFeatureCodes();

    if (
      new HashSet<>(featureCodes).size()
        != featureCodes.size()
    ) {
      throw invalidDraft();
    }

    if (
      adminRentalRepository.countActiveFeatures(
        featureCodes
      ) != featureCodes.size()
    ) {
      throw invalidDraft();
    }
  }

  private BusinessException invalidDraft() {
    return new BusinessException(
      ErrorCode.RENTAL_DRAFT_INVALID
    );
  }

  @Override
  @Transactional
  public AdminRentalUpdatedVO updateDraft(
    Long rentalId,
    AdminRentalUpdateRequest request
  ) {
    if (rentalId == null || rentalId <= 0) {
      throw new BusinessException(
        ErrorCode.BAD_REQUEST
      );
    }

    Long userId = currentUserService.requireUserId();

    Long companyId = adminRentalRepository
      .findActiveCompanyId(userId);

    if (companyId == null) {
      throw new BusinessException(
        ErrorCode.RENTAL_MANAGEMENT_FORBIDDEN
      );
    }

    AdminRentalDetailVO current =
      adminRentalRepository.findDetail(
        rentalId,
        userId,
        companyId
      );

    if (current == null) {
      throw new BusinessException(
        ErrorCode.RENTAL_NOT_FOUND
      );
    }

    boolean editableStatus =
      Integer.valueOf(0).equals(current.getStatus())
        || Integer.valueOf(3).equals(
        current.getStatus()
      );

    boolean underReview =
      Integer.valueOf(1).equals(
        current.getReviewStatus()
      );

    if (!editableStatus || underReview) {
      throw new BusinessException(
        ErrorCode.RENTAL_EDIT_NOT_ALLOWED
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

    Long agentId = request.getAgentId() == null
      ? userId
      : request.getAgentId();

    validateDraft(
      request,
      companyId,
      agentId
    );

    request.setName(request.getName().trim());
    request.setAddress(request.getAddress().trim());
    request.setFloorPlan(request.getFloorPlan().trim());

    AdminRentalUpdateRow rental =
      new AdminRentalUpdateRow();

    rental.setId(rentalId);
    rental.setCompanyId(companyId);
    rental.setAgentId(agentId);
    rental.setOperatorId(userId);
    rental.setValues(request);

    boolean updated = adminRentalRepository
      .updateDraft(rental);

    if (!updated) {
      throw new BusinessException(
        ErrorCode.RENTAL_VERSION_CONFLICT
      );
    }

    adminRentalRepository.deleteStationRelations(
      rentalId
    );

    adminRentalRepository.deleteFeatureRelations(
      rentalId
    );

    int stationCount = adminRentalRepository
      .insertStationRelations(
        rentalId,
        request.getStations()
      );

    int featureCount = adminRentalRepository
      .insertFeatureRelations(
        rentalId,
        request.getFeatureCodes()
      );

    if (
      stationCount != request.getStations().size()
        || featureCount != request.getFeatureCodes().size()
    ) {
      throw invalidDraft();
    }

    return new AdminRentalUpdatedVO(
      rentalId.toString(),
      request.getVersion() + 1
    );
  }
}
