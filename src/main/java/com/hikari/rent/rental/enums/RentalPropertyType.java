package com.hikari.rent.rental.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum RentalPropertyType {

  MANSION(1),
  APARTMENT(2),
  DETACHED_OR_OTHER(3);

  @EnumValue
  private final int value;

  RentalPropertyType(int value) {
    this.value = value;
  }
}
