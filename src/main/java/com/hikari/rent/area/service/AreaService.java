package com.hikari.rent.area.service;

import com.hikari.rent.area.vo.AreaTreeVO;
import com.hikari.rent.area.vo.AreaVO;

import java.util.List;

public interface AreaService {

  List<AreaVO> getChildren(String parentCode);

  List<AreaTreeVO> getTree();
}
