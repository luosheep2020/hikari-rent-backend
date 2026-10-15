package com.hikari.rent.rental.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RentalStatus {

  DRAFT(0),
  PUBLISHED(1),
  RENTED(2),
  ARCHIVED(3);

  @EnumValue
  @JsonValue
  private final int code;
}
