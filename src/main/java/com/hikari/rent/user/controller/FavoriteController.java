package com.hikari.rent.user.controller;


import com.hikari.rent.common.result.ApiResponse;
import com.hikari.rent.user.service.FavoriteService;
import com.hikari.rent.rental.vo.RentalListVO;

import jakarta.annotation.Resource;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user/favorites")
public class FavoriteController {

  @Resource
  private FavoriteService favoriteService;

  @GetMapping
  public ApiResponse<List<RentalListVO>> getFavorites() {
    return ApiResponse.success(
      favoriteService.getFavorites()
    );
  }

  @GetMapping("/ids")
  public ApiResponse<List<String>> getFavoriteIds() {
    return ApiResponse.success(
      favoriteService.getFavoriteIds()
    );
  }

  @PutMapping("/{rentalId}")
  public ApiResponse<Void> addFavorite(
    @PathVariable("rentalId") Long rentalId
  ) {
    favoriteService.addFavorite(rentalId);

    return ApiResponse.success();
  }

  @DeleteMapping("/{rentalId}")
  public ApiResponse<Void> removeFavorite(
    @PathVariable("rentalId") Long rentalId
  ) {
    favoriteService.removeFavorite(rentalId);

    return ApiResponse.success();
  }
}
