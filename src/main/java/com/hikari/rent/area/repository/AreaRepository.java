package com.hikari.rent.area.repository;

import com.baomidou.mybatisplus.extension.repository.IRepository;
import com.hikari.rent.area.entity.Area;
import com.hikari.rent.area.repository.model.AreaPopularityRow;

import java.util.List;

public interface AreaRepository extends IRepository<Area> {

  List<Area> findChildren(String parentCode);
  List<Area> findAllOrdered();
  List<AreaPopularityRow> findPopularityAreasByCodes(
    List<String> codes
  );
}
