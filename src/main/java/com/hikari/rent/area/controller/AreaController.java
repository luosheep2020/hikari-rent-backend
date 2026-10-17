package com.hikari.rent.area.controller;

import com.hikari.rent.area.service.AreaService;
import com.hikari.rent.area.vo.AreaTreeVO;
import com.hikari.rent.area.vo.AreaVO;
import com.hikari.rent.common.result.ApiResponse;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/areas")
public class AreaController {

  @Resource
  private  AreaService areaService;

  @GetMapping
  public ApiResponse<List<AreaVO>> getChildren(
    @RequestParam(
      name = "parentCode",
      required = false
    ) String parentCode
  ) {
    return ApiResponse.success(
      areaService.getChildren(parentCode)
    );
  }

  @GetMapping("/tree")
  public ApiResponse<List<AreaTreeVO>> getTree() {
    return ApiResponse.success(areaService.getTree());
  }
}
