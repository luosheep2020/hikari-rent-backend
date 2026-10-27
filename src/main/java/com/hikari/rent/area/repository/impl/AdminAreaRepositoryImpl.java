package com.hikari.rent.area.repository.impl;

import com.hikari.rent.area.entity.Area;
import com.hikari.rent.area.mapper.AdminAreaMapper;
import com.hikari.rent.area.repository.AdminAreaRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AdminAreaRepositoryImpl
  implements AdminAreaRepository {

  @Resource
  private AdminAreaMapper adminAreaMapper;

  @Override
  public List<Area> findAllOrdered() {
    return adminAreaMapper.selectAllOrdered();
  }

  @Override
  public Area findByCode(String code) {
    return adminAreaMapper.selectByCode(code);
  }

  @Override
  public Area findByCodeForUpdate(String code) {
    return adminAreaMapper.selectByCodeForUpdate(code);
  }

  @Override
  public Area findByCodeForShare(String code) {
    return adminAreaMapper.selectByCodeForShare(code);
  }

  @Override
  public void insert(Area area) {
    int inserted = adminAreaMapper.insert(area);

    if (inserted != 1) {
      throw new IllegalStateException(
        "Area insert did not affect exactly one record"
      );
    }
  }

  @Override
  public void update(Area area) {
    adminAreaMapper.update(area);
  }
}
