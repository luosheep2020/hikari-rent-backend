package com.hikari.rent.area.service.impl;

import com.hikari.rent.area.repository.AreaRepository;
import com.hikari.rent.area.service.AreaService;
import com.hikari.rent.area.vo.AreaVO;
import jakarta.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service

public class AreaServiceImpl implements AreaService {

  @Resource
  private AreaRepository areaRepository;

  @Override
  @Transactional(readOnly = true)
  public List<AreaVO> getChildren(String parentCode) {
    String normalizedParentCode =
      parentCode == null || parentCode.isBlank()
        ? null
        : parentCode.trim();

    return areaRepository.findChildren(normalizedParentCode)
      .stream()
      .map(area -> new AreaVO(
        area.getCode(),
        area.getName(),
        area.getParentCode(),
        area.getLevel(),
        area.getLatitude(),
        area.getLongitude()
      ))
      .toList();
  }
}
