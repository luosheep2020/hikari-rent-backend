package com.hikari.rent.area.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AdminAreaVO {

  private String code;

  private String name;

  private String parentCode;

  private Integer level;

  private BigDecimal latitude;

  private BigDecimal longitude;

  private Integer sortOrder;
}
