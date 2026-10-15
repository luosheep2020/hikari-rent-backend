package com.hikari.rent.common.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageQuery {

  @NotNull(message = "Page number is required")
  @Min(value = 1, message = "Page number must be at least 1")
  private Integer pageNum = 1;

  @NotNull(message = "Page size is required")
  @Min(value = 1, message = "Page size must be at least 1")
  @Max(value = 100, message = "Page size must not exceed 100")
  private Integer pageSize = 10;

}
