package com.hikari.rent.railway.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hikari.rent.common.enums.ErrorCode;
import com.hikari.rent.common.exception.BusinessException;
import com.hikari.rent.common.result.PageResult;
import com.hikari.rent.railway.cache.StationTreeCache;
import com.hikari.rent.railway.dto.AdminRailwayLineCreateRequest;
import com.hikari.rent.railway.dto.AdminRailwayLinePageRequest;
import com.hikari.rent.railway.dto.AdminRailwayLineStatusRequest;
import com.hikari.rent.railway.dto.AdminRailwayLineUpdateRequest;
import com.hikari.rent.railway.entity.RailwayLine;
import com.hikari.rent.railway.repository.AdminRailwayLineRepository;
import com.hikari.rent.railway.service.AdminRailwayLineService;
import com.hikari.rent.railway.vo.AdminRailwayLineCreatedVO;
import com.hikari.rent.railway.vo.AdminRailwayLineVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Locale;
import java.util.Objects;

@Slf4j
@Service
@PreAuthorize("hasRole('PLATFORM_ADMIN')")
public class AdminRailwayLineServiceImpl
  implements AdminRailwayLineService {

  @Resource
  private AdminRailwayLineRepository adminRailwayLineRepository;

  @Resource
  private StationTreeCache stationTreeCache;

  @Override
  @Transactional(readOnly = true)
  public PageResult<AdminRailwayLineVO> searchPage(
    AdminRailwayLinePageRequest request
  ) {
    IPage<RailwayLine> page =
      adminRailwayLineRepository.searchPage(request);

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
  public AdminRailwayLineVO getDetail(Long lineId) {
    validateId(lineId);

    RailwayLine line =
      adminRailwayLineRepository.findById(lineId);

    if (line == null) {
      throw lineNotFound();
    }

    return toVO(line);
  }

  @Override
  @Transactional
  public AdminRailwayLineCreatedVO create(
    AdminRailwayLineCreateRequest request
  ) {
    String operatorName =
      adminRailwayLineRepository.findOperatorName(
        request.getOperatorSourceCode()
      );

    if (operatorName == null) {
      throw new BusinessException(
        ErrorCode.RAILWAY_OPERATOR_NOT_FOUND
      );
    }

    RailwayLine line = new RailwayLine();

    line.setSourceCode(request.getSourceCode());
    line.setOperatorSourceCode(request.getOperatorSourceCode());
    line.setOperatorName(operatorName);

    applyValues(line, request);

    try {
      adminRailwayLineRepository.insert(line);
    } catch (DuplicateKeyException ex) {
      throw new BusinessException(
        ErrorCode.DATA_CONFLICT,
        "Railway line source code already exists"
      );
    }

    evictCacheAfterCommit();

    return new AdminRailwayLineCreatedVO(
      line.getId().toString()
    );
  }

  @Override
  @Transactional
  public void update(
    Long lineId,
    AdminRailwayLineUpdateRequest request
  ) {
    RailwayLine line = requireLineForUpdate(lineId);

    String previousName = line.getName();

    applyValues(line, request);

    adminRailwayLineRepository.update(line);

    if (!Objects.equals(previousName, line.getName())) {
      adminRailwayLineRepository.updateStationLineNames(
        lineId,
        line.getName()
      );
    }

    adminRailwayLineRepository.enqueueRentalSyncTasks(lineId);

    evictCacheAfterCommit();
  }

  @Override
  @Transactional
  public void updateStatus(
    Long lineId,
    AdminRailwayLineStatusRequest request
  ) {
    if (
      request == null
        || request.getStatus() == null
        || request.getStatus() < 0
        || request.getStatus() > 2
    ) {
      throw new BusinessException(ErrorCode.BAD_REQUEST);
    }

    RailwayLine line = requireLineForUpdate(lineId);

    if (Objects.equals(line.getStatus(), request.getStatus())) {
      return;
    }

    adminRailwayLineRepository.updateStatus(
      lineId,
      request.getStatus()
    );

    adminRailwayLineRepository.enqueueRentalSyncTasks(lineId);

    evictCacheAfterCommit();
  }

  private RailwayLine requireLineForUpdate(Long lineId) {
    validateId(lineId);

    RailwayLine line =
      adminRailwayLineRepository.findByIdForUpdate(lineId);

    if (line == null) {
      throw lineNotFound();
    }

    return line;
  }

  private void applyValues(
    RailwayLine line,
    AdminRailwayLineUpdateRequest request
  ) {
    line.setName(request.getName().strip());
    line.setNameKana(normalize(request.getNameKana()));
    line.setSortOrder(request.getSortOrder());

    line.setColor(
      request.getColor() == null
        ? null
        : request.getColor().toUpperCase(Locale.ROOT)
    );
  }

  private String normalize(String value) {
    if (value == null) {
      return null;
    }

    String normalized = value.strip();

    return normalized.isBlank() ? null : normalized;
  }

  private void validateId(Long lineId) {
    if (lineId == null || lineId <= 0) {
      throw new BusinessException(ErrorCode.BAD_REQUEST);
    }
  }

  private AdminRailwayLineVO toVO(RailwayLine line) {
    return new AdminRailwayLineVO(
      line.getId().toString(),
      line.getSourceCode(),
      line.getName(),
      line.getNameKana(),
      line.getOperatorSourceCode(),
      line.getOperatorName(),
      line.getColor(),
      line.getStatus(),
      line.getSortOrder(),
      line.getCreatedAt(),
      line.getUpdatedAt()
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
              "Railway changes committed, but station tree cache eviction failed",
              ex
            );
          }
        }
      }
    );
  }

  private BusinessException lineNotFound() {
    return new BusinessException(
      ErrorCode.RAILWAY_LINE_NOT_FOUND
    );
  }
}
