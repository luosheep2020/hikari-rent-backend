package com.hikari.rent.rental.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class RentalMapRequest extends RentalPageRequest {

  @NotNull(message = "North boundary is required")
  @DecimalMin(value = "-90", message = "North must be at least -90")
  @DecimalMax(value = "90", message = "North must not exceed 90")
  private BigDecimal north;

  @NotNull(message = "South boundary is required")
  @DecimalMin(value = "-90", message = "South must be at least -90")
  @DecimalMax(value = "90", message = "South must not exceed 90")
  private BigDecimal south;

  @NotNull(message = "East boundary is required")
  @DecimalMin(value = "-180", message = "East must be at least -180")
  @DecimalMax(value = "180", message = "East must not exceed 180")
  private BigDecimal east;

  @NotNull(message = "West boundary is required")
  @DecimalMin(value = "-180", message = "West must be at least -180")
  @DecimalMax(value = "180", message = "West must not exceed 180")
  private BigDecimal west;

  @AssertTrue(message = "Invalid map bounds")
  public boolean isBoundsValid() {
    if (
      north == null ||
        south == null ||
        east == null ||
        west == null
    ) {
      return true;
    }

    return north.compareTo(south) > 0
      && east.compareTo(west) > 0;
  }
}
