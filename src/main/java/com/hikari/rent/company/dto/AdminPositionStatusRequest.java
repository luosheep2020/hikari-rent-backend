package com.hikari.rent.company.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminPositionStatusRequest {

  @NotNull
  @Min(0)
  @Max(1)
  private Integer status;
}
