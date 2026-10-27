package com.hikari.rent.railway.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminRailwayLineUpdateRequest {

  @NotBlank
  @Size(max = 100)
  private String name;

  @Size(max = 150)
  private String nameKana;

  @Pattern(
    regexp = "^[0-9A-Fa-f]{6}$",
    message = "Color must contain six hexadecimal digits without #"
  )
  private String color;

  @NotNull
  @Min(0)
  private Integer sortOrder = 0;
}
