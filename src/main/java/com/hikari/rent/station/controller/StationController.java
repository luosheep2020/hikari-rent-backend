package com.hikari.rent.station.controller;

import com.hikari.rent.common.result.ApiResponse;
import com.hikari.rent.station.dto.StationSearchRequest;
import com.hikari.rent.station.service.StationService;
import com.hikari.rent.station.vo.StationOptionVO;
import com.hikari.rent.station.vo.StationSearchVO;
import com.hikari.rent.station.vo.StationTreeVO;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stations")
public class StationController {

  @Resource
  private StationService stationService;

  @GetMapping
  public ApiResponse<List<StationSearchVO>> search(
    @Valid @ModelAttribute StationSearchRequest request
  ) {
    return ApiResponse.success(
      stationService.search(request)
    );
  }

  @GetMapping("/operators")
  public ApiResponse<List<StationOptionVO>> getOperators() {
    return ApiResponse.success(
      stationService.getOperators()
    );
  }

  @GetMapping("/lines")
  public ApiResponse<List<StationOptionVO>> getLines(
    @RequestParam("operatorCode") String operatorCode
  ) {
    return ApiResponse.success(
      stationService.getLines(operatorCode)
    );
  }

  @GetMapping("/options")
  public ApiResponse<List<StationOptionVO>> getStationOptions(
    @RequestParam("lineId") Long lineId
  ) {
    return ApiResponse.success(
      stationService.getStationOptions(lineId)
    );
  }

  @GetMapping("/tree")
  public ApiResponse<List<StationTreeVO>> getTree() {
    return ApiResponse.success(
      stationService.getTree()
    );
  }
}
