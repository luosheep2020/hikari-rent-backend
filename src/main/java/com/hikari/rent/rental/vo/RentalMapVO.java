package com.hikari.rent.rental.vo;

import java.util.List;

public record RentalMapVO(
  List<RentalListVO> records,
  boolean hasMore,
  int limit
) {
}
