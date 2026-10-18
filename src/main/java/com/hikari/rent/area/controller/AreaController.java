package com.hikari.rent.area.controller;

import com.hikari.rent.area.service.AreaPopularityService;
import com.hikari.rent.area.service.AreaService;
import com.hikari.rent.area.vo.AreaTreeVO;
import com.hikari.rent.area.vo.AreaVO;
import com.hikari.rent.area.vo.PopularAreaVO;
import com.hikari.rent.common.result.ApiResponse;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/areas")
public class AreaController {

  @Resource
  private  AreaService areaService;
  @Resource
  private AreaPopularityService areaPopularityService;
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

  @GetMapping("/popular")
  public ApiResponse<List<PopularAreaVO>> getPopularAreas() {
    return ApiResponse.success(
      areaPopularityService.getPopularAreas()
    );
  }

  @PostMapping("/{code}/search-events")
  public ApiResponse<Void> recordAreaSearch(
    @PathVariable("code") String code
  ) {
    areaPopularityService.recordSearch(code);

    return ApiResponse.success();
  }
}
