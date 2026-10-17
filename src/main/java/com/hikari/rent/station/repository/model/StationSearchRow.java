package com.hikari.rent.station.repository.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class StationSearchRow {

  private Long id;

  private String name;

  private String lineName;

  private String prefectureCode;

  private String areaCode;

  private BigDecimal latitude;

  private BigDecimal longitude;
}
