package com.hikari.rent.user.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FavoriteMapper {

  List<Long> selectRentalIds(
    @Param("userId") Long userId
  );

  int insertFavorite(
    @Param("userId") Long userId,
    @Param("rentalId") Long rentalId
  );

  int deleteFavorite(
    @Param("userId") Long userId,
    @Param("rentalId") Long rentalId
  );
}
