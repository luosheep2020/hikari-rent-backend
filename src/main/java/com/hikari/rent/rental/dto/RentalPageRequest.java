package com.hikari.rent.rental.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

  @Min(value = 1, message = "Page must be at least 1")
  private Integer page = 1;

  @Min(value = 1, message = "Page size must be at least 1")
  @Max(value = 50, message = "Page size must not exceed 50")
  private Integer pageSize = 6;

  @Size(max = 100, message = "Keyword must not exceed 100 characters")
  private String keyword;

  @Pattern(
    regexp = "\\d{5}",
    message = "Area code must be a five-digit municipality code"
  )
  private String areaCode;

  @Positive(message = "Station ID must be positive")
  private Long stationId;

  @PositiveOrZero(message = "Minimum rent must not be negative")
  @Max(value = 4294967295L, message = "Minimum rent is too large")
  private Long minRent;

  @PositiveOrZero(message = "Maximum rent must not be negative")
  @Max(value = 4294967295L, message = "Maximum rent is too large")
  private Long maxRent;

  @Size(max = 20, message = "Too many floor plans")
  private List<
    @Pattern(
      regexp = "1R|[1-9](K|DK|LDK)",
      message = "Invalid floor plan"
    )
      String
    > floorPlans = new ArrayList<>();

  @Min(value = 0, message = "Building age must not be negative")
  @Max(value = 200, message = "Building age is too large")
  private Integer maxBuildingAge;

  @Min(value = 1, message = "Walking time must be positive")
  @Max(value = 180, message = "Walking time is too large")
  private Integer maxWalkingMinutes;

  // 设备和入住条件都用稳定代码查询。
  @Size(max = 30, message = "Too many features")
  private List<
    @Pattern(
      regexp = "[a-z][a-z0-9_]{0,63}",
      message = "Invalid feature code"
    )
      String
    > featureCodes = new ArrayList<>();

  @Pattern(
    regexp = "recommended|rentAsc|rentDesc|areaDesc|walkingAsc",
    message = "Invalid sort option"
  )
  private String sort = "recommended";

  @JsonIgnore
  @AssertTrue(message = "Maximum rent must be greater than or equal to minimum rent")
  public boolean isRentRangeValid() {
    return minRent == null
      || maxRent == null
      || minRent <= maxRent;
  }
}
