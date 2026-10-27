package com.hikari.rent.railway.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hikari.rent.area.entity.Area;
import com.hikari.rent.area.repository.AdminAreaRepository;
import com.hikari.rent.common.enums.ErrorCode;
import com.hikari.rent.common.exception.BusinessException;
import com.hikari.rent.common.result.PageResult;
import com.hikari.rent.railway.cache.StationTreeCache;
import com.hikari.rent.railway.dto.AdminStationCreateRequest;
import com.hikari.rent.railway.dto.AdminStationPageRequest;
import com.hikari.rent.railway.dto.AdminStationStatusRequest;
import com.hikari.rent.railway.dto.AdminStationUpdateRequest;
import com.hikari.rent.railway.entity.RailwayLine;
import com.hikari.rent.railway.entity.Station;
import com.hikari.rent.railway.repository.AdminRailwayLineRepository;
import com.hikari.rent.railway.repository.AdminStationRepository;
import com.hikari.rent.railway.repository.model.AdminStationRow;
import com.hikari.rent.railway.service.AdminStationService;
import com.hikari.rent.railway.vo.AdminStationCreatedVO;
import com.hikari.rent.railway.vo.AdminStationVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Objects;

@Slf4j
@Service
@PreAuthorize("hasRole('PLATFORM_ADMIN')")
public class AdminStationServiceImpl
  implements AdminStationService {

  @Resource
  private AdminStationRepository adminStationRepository;

  @Resource
  private AdminRailwayLineRepository adminRailwayLineRepository;

  @Resource
  private AdminAreaRepository adminAreaRepository;

  @Resource
  private StationTreeCache stationTreeCache;

  @Override
  @Transactional(readOnly = true)
  public PageResult<AdminStationVO> searchPage(
    AdminStationPageRequest request
  ) {
    IPage<AdminStationRow> page =
      adminStationRepository.searchPage(request);

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
  public AdminStationVO getDetail(Long stationId) {
    validateId(stationId);

    AdminStationRow station =
      adminStationRepository.findDetail(stationId);

    if (station == null) {
      throw stationNotFound();
    }

    return toVO(station);
  }

  @Override
  @Transactional
  public AdminStationCreatedVO create(
    AdminStationCreateRequest request
  ) {
    RailwayLine line = requireLineForUpdate(
      request.getLineId()
    );

    validateLocation(request);

    Station station = new Station();

    station.setSourceCode(request.getSourceCode());
    station.setStationGroupCode(request.getStationGroupCode());

    station.setLineId(line.getId());
    station.setLineName(line.getName());

    applyValues(station, request);

    try {
      adminStationRepository.insert(station);
    } catch (DuplicateKeyException ex) {
      throw new BusinessException(
        ErrorCode.DATA_CONFLICT,
        "Station source code already exists"
      );
    }

    evictCacheAfterCommit();

    return new AdminStationCreatedVO(
      station.getId().toString()
    );
  }

  @Override
  @Transactional
  public void update(
    Long stationId,
    AdminStationUpdateRequest request
  ) {
    Station station = requireStationForUpdate(stationId);

    validateLocation(request);

    applyValues(station, request);

    adminStationRepository.update(station);

    adminStationRepository.enqueueRentalSyncTasks(stationId);

    evictCacheAfterCommit();
  }

  @Override
  @Transactional
  public void updateStatus(
    Long stationId,
    AdminStationStatusRequest request
  ) {
    if (
      request == null
        || request.getStatus() == null
        || request.getStatus() < 0
        || request.getStatus() > 2
    ) {
      throw new BusinessException(ErrorCode.BAD_REQUEST);
    }

    Station station = requireStationForUpdate(stationId);

    if (
      Objects.equals(
        station.getStatus(),
        request.getStatus()
      )
    ) {
      return;
    }

    adminStationRepository.updateStatus(
      stationId,
      station.getLineId(),
      request.getStatus()
    );

    adminStationRepository.enqueueRentalSyncTasks(stationId);

    evictCacheAfterCommit();
  }

  private Station requireStationForUpdate(Long stationId) {
    validateId(stationId);

    Station initial =
      adminStationRepository.findById(stationId);

    if (initial == null) {
      throw stationNotFound();
    }

    // Match the line-first lock order used by line maintenance.
    RailwayLine line = requireLineForUpdate(
      initial.getLineId()
    );

    Station current =
      adminStationRepository.findByIdForUpdate(
        stationId,
        line.getId()
      );

    if (current == null) {
      throw new BusinessException(
        ErrorCode.DATA_CONFLICT,
        "Station changed; refresh and try again"
      );
    }

    current.setLineName(line.getName());

    return current;
  }

  private RailwayLine requireLineForUpdate(Long lineId) {
    if (lineId == null || lineId <= 0) {
      throw new BusinessException(ErrorCode.BAD_REQUEST);
    }

    RailwayLine line =
      adminRailwayLineRepository.findByIdForUpdate(lineId);

    if (line == null) {
      throw new BusinessException(
        ErrorCode.RAILWAY_LINE_NOT_FOUND
      );
    }

    return line;
  }

  private void validateLocation(
    AdminStationUpdateRequest request
  ) {
    boolean hasLatitude = request.getLatitude() != null;
    boolean hasLongitude = request.getLongitude() != null;

    if (hasLatitude != hasLongitude) {
      throw new BusinessException(
        ErrorCode.BAD_REQUEST,
        "Latitude and longitude must be provided together"
      );
    }

    String prefectureCode = request.getPrefectureCode();
    String areaCode = request.getAreaCode();

    if (prefectureCode == null) {
      if (areaCode != null) {
        throw new BusinessException(
          ErrorCode.BAD_REQUEST,
          "Prefecture code is required when municipality is specified"
        );
      }

      return;
    }

    Area prefecture =
      adminAreaRepository.findByCodeForShare(prefectureCode);

    if (
      prefecture == null
        || !Integer.valueOf(2).equals(prefecture.getLevel())
    ) {
      throw new BusinessException(
        ErrorCode.INVALID_AREA_LEVEL,
        "Prefecture must reference a level-2 area"
      );
    }

    if (areaCode == null) {
      return;
    }

    Area municipality =
      adminAreaRepository.findByCodeForShare(areaCode);

    if (
      municipality == null
        || !Integer.valueOf(3).equals(municipality.getLevel())
        || !Objects.equals(
        municipality.getParentCode(),
        prefectureCode
      )
    ) {
      throw new BusinessException(
        ErrorCode.BAD_REQUEST,
        "Municipality must belong to the selected prefecture"
      );
    }
  }

  private void applyValues(
    Station station,
    AdminStationUpdateRequest request
  ) {
    station.setName(request.getName().strip());
    station.setNameKana(normalize(request.getNameKana()));

    station.setPrefectureCode(request.getPrefectureCode());
    station.setAreaCode(request.getAreaCode());

    station.setAddress(normalize(request.getAddress()));
    station.setPostalCode(normalize(request.getPostalCode()));

    station.setLatitude(request.getLatitude());
    station.setLongitude(request.getLongitude());
    station.setSortOrder(request.getSortOrder());
  }

  private String normalize(String value) {
    if (value == null) {
      return null;
    }

    String normalized = value.strip();

    return normalized.isBlank() ? null : normalized;
  }

  private void validateId(Long stationId) {
    if (stationId == null || stationId <= 0) {
      throw new BusinessException(ErrorCode.BAD_REQUEST);
    }
  }

  private AdminStationVO toVO(AdminStationRow station) {
    return new AdminStationVO(
      station.getId().toString(),
      station.getSourceCode(),
      station.getStationGroupCode(),
      station.getName(),
      station.getNameKana(),
      station.getLineId().toString(),
      station.getLineName(),
      station.getOperatorSourceCode(),
      station.getOperatorName(),
      station.getPrefectureCode(),
      station.getPrefectureName(),
      station.getAreaCode(),
      station.getAreaName(),
      station.getAddress(),
      station.getPostalCode(),
      station.getLatitude(),
      station.getLongitude(),
      station.getStatus(),
      station.getSortOrder(),
      station.getCreatedAt(),
      station.getUpdatedAt()
    );
  }

  private void evictCacheAfterCommit() {
    TransactionSynchronizationManager.registerSynchronization(
      new TransactionSynchronization() {
        @Override
        public void afterCommit() {
          try {
            stationTreeCache.evict();
          } catch (RuntimeException ex) {
            log.error(
              "Station changes committed, but station tree cache eviction failed",
              ex
            );
          }
        }
      }
    );
  }

  private BusinessException stationNotFound() {
    return new BusinessException(
      ErrorCode.STATION_NOT_FOUND
    );
  }
}
