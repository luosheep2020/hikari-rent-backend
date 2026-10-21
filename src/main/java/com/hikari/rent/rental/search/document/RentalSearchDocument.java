package com.hikari.rent.rental.search.document;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class RentalSearchDocument {

  private String id;

  private Long rentalId;

  private String name;

  private String address;

  private String description;

  private String areaCode;

  private List<String> areaPathCodes = List.of();

  private Integer propertyType;

  private Long monthlyRent;

  private Long managementFee;

  private String floorPlan;

  private BigDecimal area;

  // ISO date, for example: 2021-04-01.
  private String builtDate;

  private List<String> featureCodes = List.of();

  private List<Station> stations = List.of();

  private Location location;

  // ISO UTC timestamp, for example: 2026-10-01T00:00:00Z.
  private String publishedAt;

  private String companyId;

  private String agentId;

  @Getter
  @Setter
  public static class Station {

    private String id;

    private String name;

    private Integer walkingMinutes;
  }

  @Getter
  @Setter
  public static class Location {

    private Double lat;

    private Double lon;
  }
}
