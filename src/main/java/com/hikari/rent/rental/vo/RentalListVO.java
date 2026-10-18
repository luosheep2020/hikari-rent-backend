package com.hikari.rent.rental.vo;


import lombok.Getter;
import lombok.Setter;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class RentalListVO {

  @JsonSerialize(using = ToStringSerializer.class)
  private Long id;

  private String areaCode;

  private String name;

  private Integer propertyType;

  private String address;

  // 文件服务生成的可访问地址。
  private String image;

  private String floorPlanImage;

  private Integer imageCount;

  private Long monthlyRent;

  private Long managementFee;

  private BigDecimal depositMonths;

  private BigDecimal keyMoneyMonths;

  private String floorPlan;

  // 对应数据库 area_sqm，保持前端字段名 area。
  private BigDecimal area;

  // 默认展示的最近车站。
  private String station;

  private Integer walkingMinutes;

  private List<RentalStationVO> stations = new ArrayList<>();

  private Integer floor;

  private Integer totalFloors;

  private Integer builtYear;

  private Integer builtMonth;

  private BigDecimal latitude;

  private BigDecimal longitude;

  private List<String> amenities = new ArrayList<>();

  private List<String> conditions = new ArrayList<>();

  // 根据 published_at 计算。
  private Boolean isNew;
}
