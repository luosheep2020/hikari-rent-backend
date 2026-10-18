package com.hikari.rent.user.service.impl;


import com.hikari.rent.auth.service.CurrentUserService;

import com.hikari.rent.common.enums.ErrorCode;
import com.hikari.rent.common.exception.BusinessException;

import com.hikari.rent.user.repository.FavoriteRepository;
import com.hikari.rent.user.service.FavoriteService;

import com.hikari.rent.rental.service.RentalService;
import com.hikari.rent.rental.vo.RentalListVO;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FavoriteServiceImpl
  implements FavoriteService {

  @Resource
  private CurrentUserService currentUserService;

  @Resource
  private FavoriteRepository favoriteRepository;

  @Resource
  private RentalService rentalService;

  @Override
  @Transactional(readOnly = true)
  public List<RentalListVO> getFavorites() {
    Long userId = currentUserService.requireUserId();

    List<Long> rentalIds =
      favoriteRepository.findRentalIds(userId);

    return rentalService.getPublicListByIds(rentalIds);
  }

  @Override
  @Transactional(readOnly = true)
  public List<String> getFavoriteIds() {
    Long userId = currentUserService.requireUserId();

    return favoriteRepository.findRentalIds(userId)
      .stream()
      .map(String::valueOf)
      .toList();
  }

  @Override
  @Transactional
  public void addFavorite(Long rentalId) {
    Long userId = currentUserService.requireUserId();

    validateRentalId(rentalId);

    // Reuse the existing public-detail visibility validation.
    rentalService.getDetail(rentalId);

    favoriteRepository.addFavorite(
      userId,
      rentalId
    );
  }

  @Override
  @Transactional
  public void removeFavorite(Long rentalId) {
    Long userId = currentUserService.requireUserId();

    validateRentalId(rentalId);

    // Allow removal even if the rental is no longer public.
    favoriteRepository.removeFavorite(
      userId,
      rentalId
    );
  }

  private void validateRentalId(Long rentalId) {
    if (rentalId == null || rentalId <= 0) {
      throw new BusinessException(
        ErrorCode.BAD_REQUEST
      );
    }
  }
}
