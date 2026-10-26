package com.hikari.rent.company.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminCompanyPageRequest {

  @Min(1)
  private long page = 1;

  @Min(1)
  @Max(100)
  private long pageSize = 20;

  @Size(max = 150)
  private String keyword;

  @Min(0)
  @Max(1)
  private Integer status;
}
