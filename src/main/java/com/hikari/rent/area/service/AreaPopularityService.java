package com.hikari.rent.area.service;

import com.hikari.rent.area.vo.PopularAreaVO;

import java.util.List;

public interface AreaPopularityService {

  List<PopularAreaVO> getPopularAreas();

  void recordSearch(String areaCode);
}
