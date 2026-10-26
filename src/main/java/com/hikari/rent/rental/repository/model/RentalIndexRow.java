package com.hikari.rent.rental.repository.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class RentalIndexRow {

  private Long id;

  private String name;

  private String address;

  private String description;

  private String areaCode;

  private Integer propertyType;

  private Long monthlyRent;

  private Long managementFee;

  private String floorPlan;

  private BigDecimal area;

  private Integer builtYear;

  private Integer builtMonth;

  private BigDecimal latitude;

  private BigDecimal longitude;

  private LocalDateTime publishedAt;

  private Long companyId;

  private Long agentId;
}
