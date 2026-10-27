package com.hikari.rent.railway.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminStationStatusRequest {

  @NotNull
  @Min(0)
  @Max(2)
  private Integer status;
}
