package com.hikari.rent.area.service;

import com.hikari.rent.area.dto.AdminAreaCreateRequest;
import com.hikari.rent.area.dto.AdminAreaUpdateRequest;
import com.hikari.rent.area.vo.AdminAreaTreeVO;
import com.hikari.rent.area.vo.AdminAreaVO;

import java.util.List;

public interface AdminAreaService {

  List<AdminAreaTreeVO> getTree();

  AdminAreaVO getDetail(String code);

  AdminAreaVO create(AdminAreaCreateRequest request);

  void update(
    String code,
    AdminAreaUpdateRequest request
  );
}
