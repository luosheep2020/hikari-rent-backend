package com.hikari.rent.area.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminAreaCreateRequest
  extends AdminAreaUpdateRequest {

  @NotBlank
  @Pattern(
    regexp = "^[A-Za-z0-9_-]{1,20}$",
    message = "Invalid area code"
  )
  private String code;

  @Pattern(
    regexp = "^[A-Za-z0-9_-]{1,20}$",
    message = "Invalid parent area code"
  )
  private String parentCode;

  @NotNull
  @Min(1)
  @Max(3)
  private Integer level;
}
