package com.hikari.rent.rental.vo.admin;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class AdminRentalListVO {

  private String id;

  private String name;

  private String areaCode;

  private String address;

  private Integer propertyType;

  private Long monthlyRent;

  private Long managementFee;

  private String floorPlan;

  private BigDecimal area;

  private Integer status;

  private Integer reviewStatus;

  private String agentId;

  private String agentName;

  private LocalDateTime publishedAt;

  private LocalDateTime updatedAt;
}
