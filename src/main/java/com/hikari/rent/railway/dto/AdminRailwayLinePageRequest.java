package com.hikari.rent.railway.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminRailwayLinePageRequest {

  @Min(1)
  private long page = 1;

  @Min(1)
  @Max(100)
  private long pageSize = 20;

  @Size(max = 100)
  private String keyword;

  @Pattern(regexp = "^[A-Za-z0-9_-]{1,20}$")
  private String operatorSourceCode;

  @Min(0)
  @Max(2)
  private Integer status;
}
