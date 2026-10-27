package com.hikari.rent.railway.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminRailwayLineCreateRequest
  extends AdminRailwayLineUpdateRequest {

  @Pattern(regexp = "^[A-Za-z0-9_-]{1,20}$")
  private String sourceCode;

  @NotBlank
  @Pattern(regexp = "^[A-Za-z0-9_-]{1,20}$")
  private String operatorSourceCode;
}
