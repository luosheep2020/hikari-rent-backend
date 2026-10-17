package com.hikari.rent.area.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class AreaTreeVO {

  private String code;

  private String name;

  private Integer level;

  private BigDecimal latitude;

  private BigDecimal longitude;

  private List<AreaTreeVO> children = new ArrayList<>();
}
