package com.hikari.rent.railway.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminStationCreateRequest
  extends AdminStationUpdateRequest {

  @Pattern(regexp = "^[A-Za-z0-9_-]{1,20}$")
  private String sourceCode;

  @Pattern(regexp = "^[A-Za-z0-9_-]{1,20}$")
  private String stationGroupCode;

  @NotNull
  @Positive
  private Long lineId;
}
