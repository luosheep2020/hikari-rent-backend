package com.hikari.rent.area.repository;

import com.hikari.rent.area.entity.Area;

import java.util.List;

public interface AdminAreaRepository {

  List<Area> findAllOrdered();

  Area findByCode(String code);

  Area findByCodeForUpdate(String code);

  Area findByCodeForShare(String code);

  void insert(Area area);

  void update(Area area);
}
