package com.hikari.rent.user.repository;

import java.util.List;

public interface FavoriteRepository {

  List<Long> findRentalIds(Long userId);

  void addFavorite(Long userId, Long rentalId);

  void removeFavorite(Long userId, Long rentalId);
}
