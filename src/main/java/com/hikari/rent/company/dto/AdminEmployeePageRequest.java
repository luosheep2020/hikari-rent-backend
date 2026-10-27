package com.hikari.rent.company.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminEmployeePageRequest {

  @Min(1)
  private long page = 1;

  @Min(1)
  @Max(100)
  private long pageSize = 20;

  @Positive
  private Long companyId;

  @Positive
  private Long departmentId;

  @Size(max = 100)
  private String keyword;

  @Min(0)
  @Max(1)
  private Integer status;
}
