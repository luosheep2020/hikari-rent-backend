package com.hikari.rent.user.service;

import com.hikari.rent.rental.vo.RentalListVO;

import java.util.List;

public interface FavoriteService {

  List<RentalListVO> getFavorites();

  List<String> getFavoriteIds();

  void addFavorite(Long rentalId);

  void removeFavorite(Long rentalId);
}
