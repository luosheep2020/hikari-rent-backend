package com.hikari.rent.user.repository.impl;

import com.hikari.rent.user.mapper.FavoriteMapper;
import com.hikari.rent.user.repository.FavoriteRepository;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FavoriteRepositoryImpl
  implements FavoriteRepository {

  @Resource
  private FavoriteMapper favoriteMapper;

  @Override
  public List<Long> findRentalIds(Long userId) {
    return favoriteMapper.selectRentalIds(userId);
  }

  @Override
  public void addFavorite(
    Long userId,
    Long rentalId
  ) {
    favoriteMapper.insertFavorite(userId, rentalId);
  }

  @Override
  public void removeFavorite(
    Long userId,
    Long rentalId
  ) {
    favoriteMapper.deleteFavorite(userId, rentalId);
  }
}
