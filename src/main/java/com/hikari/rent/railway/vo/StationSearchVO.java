package com.hikari.rent.railway.vo;

import java.math.BigDecimal;

public record StationSearchVO(
  String id,
  String name,
  String lineName,
  String prefectureCode,
  String areaCode,
  BigDecimal latitude,
  BigDecimal longitude
) {
}
