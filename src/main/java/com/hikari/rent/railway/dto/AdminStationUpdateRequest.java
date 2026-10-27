package com.hikari.rent.railway.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AdminStationUpdateRequest {

  @NotBlank
  @Size(max = 100)
  private String name;

  @Size(max = 150)
  private String nameKana;

  @Pattern(regexp = "^\\d{2}$")
  private String prefectureCode;

  @Pattern(regexp = "^\\d{5}$")
  private String areaCode;

  @Size(max = 255)
  private String address;

  @Size(max = 10)
  private String postalCode;

  @DecimalMin("-90")
  @DecimalMax("90")
  @Digits(integer = 3, fraction = 7)
  private BigDecimal latitude;

  @DecimalMin("-180")
  @DecimalMax("180")
  @Digits(integer = 3, fraction = 7)
  private BigDecimal longitude;

  @NotNull
  @Min(0)
  private Integer sortOrder = 0;
}
