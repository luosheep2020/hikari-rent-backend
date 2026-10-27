package com.hikari.rent.area.controller;

import com.hikari.rent.area.dto.AdminAreaCreateRequest;
import com.hikari.rent.area.dto.AdminAreaUpdateRequest;
import com.hikari.rent.area.service.AdminAreaService;
import com.hikari.rent.area.vo.AdminAreaTreeVO;
import com.hikari.rent.area.vo.AdminAreaVO;
import com.hikari.rent.common.result.ApiResponse;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/areas")
public class AdminAreaController {

  @Resource
  private AdminAreaService adminAreaService;

  @GetMapping("/tree")
  public ApiResponse<List<AdminAreaTreeVO>> getTree() {
    return ApiResponse.success(
      adminAreaService.getTree()
    );
  }

  @GetMapping("/{code}")
  public ApiResponse<AdminAreaVO> getDetail(
    @PathVariable("code") String code
  ) {
    return ApiResponse.success(
      adminAreaService.getDetail(code)
    );
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ApiResponse<AdminAreaVO> create(
    @Valid @RequestBody AdminAreaCreateRequest request
  ) {
    return ApiResponse.success(
      adminAreaService.create(request)
    );
  }

  @PutMapping("/{code}")
  public ApiResponse<Void> update(
    @PathVariable("code") String code,
    @Valid @RequestBody AdminAreaUpdateRequest request
  ) {
    adminAreaService.update(code, request);

    return ApiResponse.success();
  }
}
