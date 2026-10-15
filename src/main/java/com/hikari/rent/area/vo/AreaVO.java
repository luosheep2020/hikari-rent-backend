package com.hikari.rent.area.vo;

import java.math.BigDecimal;

public record AreaVO(
  String code,
  String name,
  String parentCode,
  Integer level,
  BigDecimal latitude,
  BigDecimal longitude
) {
}
