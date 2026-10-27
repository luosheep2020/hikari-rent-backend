package com.hikari.rent.area.service.impl;

import com.hikari.rent.area.cache.AreaTreeCache;
import com.hikari.rent.area.dto.AdminAreaCreateRequest;
import com.hikari.rent.area.dto.AdminAreaUpdateRequest;
import com.hikari.rent.area.entity.Area;
import com.hikari.rent.area.repository.AdminAreaRepository;
import com.hikari.rent.area.service.AdminAreaService;
import com.hikari.rent.area.vo.AdminAreaTreeVO;
import com.hikari.rent.area.vo.AdminAreaVO;
import com.hikari.rent.common.enums.ErrorCode;
import com.hikari.rent.common.exception.BusinessException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@PreAuthorize("hasRole('PLATFORM_ADMIN')")
public class AdminAreaServiceImpl implements AdminAreaService {

  @Resource
  private AdminAreaRepository adminAreaRepository;

  @Resource
  private AreaTreeCache areaTreeCache;

  @Override
  @Transactional(readOnly = true)
  public List<AdminAreaTreeVO> getTree() {
    List<Area> areas = adminAreaRepository.findAllOrdered();

    Map<String, AdminAreaTreeVO> nodes =
      new LinkedHashMap<>();

    for (Area area : areas) {
      AdminAreaTreeVO node = new AdminAreaTreeVO();

      fillVO(node, area);

      nodes.put(area.getCode(), node);
    }

    List<AdminAreaTreeVO> roots = new ArrayList<>();

    for (Area area : areas) {
      AdminAreaTreeVO node = nodes.get(area.getCode());

      if (area.getParentCode() == null) {
        if (!Integer.valueOf(1).equals(area.getLevel())) {
          throw invalidHierarchy();
        }

        roots.add(node);
        continue;
      }

      AdminAreaTreeVO parent =
        nodes.get(area.getParentCode());

      if (
        parent == null
          || parent.getLevel() == null
          || area.getLevel() == null
          || area.getLevel() < 2
          || area.getLevel() > 3
          || parent.getLevel() != area.getLevel() - 1
      ) {
        throw invalidHierarchy();
      }

      parent.getChildren().add(node);
    }

    return roots;
  }

  @Override
  @Transactional(readOnly = true)
  public AdminAreaVO getDetail(String code) {
    validateCode(code);

    Area area = adminAreaRepository.findByCode(code);

    if (area == null) {
      throw new BusinessException(ErrorCode.AREA_NOT_FOUND);
    }

    return toVO(area);
  }

  @Override
  @Transactional
  public AdminAreaVO create(
    AdminAreaCreateRequest request
  ) {
    validateCoordinates(request);
    validateHierarchy(request);

    Area area = new Area();
    area.setCode(request.getCode());
    area.setParentCode(request.getParentCode());
    area.setLevel(request.getLevel());

    applyValues(area, request);

    try {
      adminAreaRepository.insert(area);
    } catch (DuplicateKeyException ex) {
      throw new BusinessException(
        ErrorCode.DATA_CONFLICT,
        "Area code already exists"
      );
    }

    evictCacheAfterCommit();

    return toVO(area);
  }

  @Override
  @Transactional
  public void update(
    String code,
    AdminAreaUpdateRequest request
  ) {
    validateCode(code);
    validateCoordinates(request);

    Area current =
      adminAreaRepository.findByCodeForUpdate(code);

    if (current == null) {
      throw new BusinessException(ErrorCode.AREA_NOT_FOUND);
    }

    applyValues(current, request);

    adminAreaRepository.update(current);

    evictCacheAfterCommit();
  }

  private void validateHierarchy(
    AdminAreaCreateRequest request
  ) {
    Integer level = request.getLevel();

    if (level == null || level < 1 || level > 3) {
      throw new BusinessException(ErrorCode.INVALID_AREA_LEVEL);
    }

    validateCode(request.getCode());

    if (level == 1) {
      if (request.getParentCode() != null) {
        throw new BusinessException(
          ErrorCode.BAD_REQUEST,
          "Root areas must not have a parent"
        );
      }

      return;
    }

    String parentCode = request.getParentCode();

    if (
      parentCode == null
        || Objects.equals(request.getCode(), parentCode)
    ) {
      throw new BusinessException(
        ErrorCode.BAD_REQUEST,
        "A valid parent area is required"
      );
    }

    validateCode(parentCode);

    Area parent =
      adminAreaRepository.findByCodeForShare(parentCode);

    if (parent == null) {
      throw new BusinessException(
        ErrorCode.AREA_NOT_FOUND,
        "Parent area not found"
      );
    }

    if (!Integer.valueOf(level - 1).equals(parent.getLevel())) {
      throw new BusinessException(
        ErrorCode.INVALID_AREA_LEVEL,
        "Parent area must be exactly one level above"
      );
    }

    if (
      level == 2
        && !request.getCode().matches("\\d{2}")
    ) {
      throw new BusinessException(
        ErrorCode.BAD_REQUEST,
        "Prefecture codes must contain two digits"
      );
    }

    if (
      level == 3
        && (
        !request.getCode().matches("\\d{5}")
          || !request.getCode().startsWith(parentCode)
      )
    ) {
      throw new BusinessException(
        ErrorCode.BAD_REQUEST,
        "Municipality codes must contain five digits and match the prefecture"
      );
    }
  }

  private void validateCoordinates(
    AdminAreaUpdateRequest request
  ) {
    boolean hasLatitude = request.getLatitude() != null;
    boolean hasLongitude = request.getLongitude() != null;

    if (hasLatitude != hasLongitude) {
      throw new BusinessException(
        ErrorCode.BAD_REQUEST,
        "Latitude and longitude must be provided together"
      );
    }
  }

  private void validateCode(String code) {
    if (
      code == null
        || !code.matches("[A-Za-z0-9_-]{1,20}")
    ) {
      throw new BusinessException(
        ErrorCode.BAD_REQUEST,
        "Invalid area code"
      );
    }
  }

  private void applyValues(
    Area area,
    AdminAreaUpdateRequest request
  ) {
    area.setName(request.getName().strip());
    area.setLatitude(request.getLatitude());
    area.setLongitude(request.getLongitude());
    area.setSortOrder(request.getSortOrder());
  }

  private AdminAreaVO toVO(Area area) {
    AdminAreaVO vo = new AdminAreaVO();

    fillVO(vo, area);

    return vo;
  }

  private void fillVO(AdminAreaVO vo, Area area) {
    vo.setCode(area.getCode());
    vo.setName(area.getName());
    vo.setParentCode(area.getParentCode());
    vo.setLevel(area.getLevel());
    vo.setLatitude(area.getLatitude());
    vo.setLongitude(area.getLongitude());
    vo.setSortOrder(area.getSortOrder());
  }

  private void evictCacheAfterCommit() {
    TransactionSynchronizationManager.registerSynchronization(
      new TransactionSynchronization() {
        @Override
        public void afterCommit() {
          try {
            areaTreeCache.evict();
          } catch (RuntimeException ex) {
            // The database change has already committed.
            log.error(
              "Area changes committed, but area tree cache eviction failed",
              ex
            );
          }
        }
      }
    );
  }

  private BusinessException invalidHierarchy() {
    return new BusinessException(
      ErrorCode.DATA_CONFLICT,
      "Stored area hierarchy is invalid"
    );
  }
}
