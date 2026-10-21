package com.hikari.rent.rental.dto.admin;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminRentalPageRequest {

  @Min(1)
  private long page = 1;

  @Min(1)
  @Max(100)
  private long pageSize = 20;

  @Size(max = 100)
  private String keyword;

  @Pattern(
    regexp = "^\\d{5}$",
    message = "Area code must contain 5 digits"
  )
  private String areaCode;

  @Min(1)
  @Max(3)
  private Integer propertyType;

  @Min(0)
  @Max(3)
  private Integer status;

  @Min(0)
  @Max(3)
  private Integer reviewStatus;
}
