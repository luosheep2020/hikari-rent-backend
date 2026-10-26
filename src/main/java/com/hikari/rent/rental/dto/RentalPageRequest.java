package com.hikari.rent.rental.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class RentalPageRequest {

  @Min(1)
  private int page = 1;

  @Min(1)
  @Max(100)
  private int pageSize = 20;

  @Size(max = 100)
  private String keyword;

  @Size(max = 20)
  private String areaCode;

  @PositiveOrZero
  private Long minRent;

  @PositiveOrZero
  private Long maxRent;

  @Min(1)
  @Max(3)
  private Integer propertyType;

  private List<String> floorPlans =
    new ArrayList<>();

  @Min(0)
  @Max(200)
  private Integer maxBuildingAge;

  @Positive
  private Long stationId;

  @PositiveOrZero
  private Integer maxWalkingMinutes;

  private List<String> featureCodes =
    new ArrayList<>();

  @Pattern(
    regexp = "recommended|rentAsc|rentDesc|areaDesc|walkingAsc",
    message = "Unsupported rental sort"
  )
  private String sort = "recommended";

  @AssertTrue(
    message = "minRent must not exceed maxRent"
  )
  public boolean isRentRangeValid() {
    return minRent == null
      || maxRent == null
      || minRent <= maxRent;
  }
}
