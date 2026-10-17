package com.hikari.rent.area.service.impl;

import com.hikari.rent.area.cache.AreaTreeCache;
import com.hikari.rent.area.entity.Area;
import com.hikari.rent.area.repository.AreaRepository;
import com.hikari.rent.area.service.AreaService;
import com.hikari.rent.area.vo.AreaTreeVO;
import com.hikari.rent.area.vo.AreaVO;
import jakarta.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service

public class AreaServiceImpl implements AreaService {

  @Resource
  private AreaRepository areaRepository;
  @Resource
  private  AreaTreeCache areaTreeCache;
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



  @Override
  public List<AreaTreeVO> getTree() {
    var cached = areaTreeCache.get();
    if (cached.isPresent()) {
      return cached.get();
    }
    List<AreaTreeVO> tree = loadTreeFromDatabase();
    areaTreeCache.put(tree);
    return tree;
  }

  private List<AreaTreeVO> loadTreeFromDatabase() {
    List<Area> areas = areaRepository.findAllOrdered();
    Map<String, AreaTreeVO> nodes = new HashMap<>();
    List<AreaTreeVO> roots = new ArrayList<>();
    for (Area area : areas) {
      AreaTreeVO node = new AreaTreeVO();
      node.setCode(area.getCode());
      node.setName(area.getName());
      node.setLevel(area.getLevel());
      node.setLatitude(area.getLatitude());
      node.setLongitude(area.getLongitude());

      nodes.put(area.getCode(), node);
    }

    for (Area area : areas) {
      AreaTreeVO node = nodes.get(area.getCode());
      if (area.getParentCode() == null) {
        roots.add(node);
        continue;
      }
      AreaTreeVO parent = nodes.get(area.getParentCode());

      if (parent == null) {
        throw new IllegalStateException(
          "Area parent not found: " + area.getCode()
        );
      }
      parent.getChildren().add(node);
    }
    return roots;
  }
}
