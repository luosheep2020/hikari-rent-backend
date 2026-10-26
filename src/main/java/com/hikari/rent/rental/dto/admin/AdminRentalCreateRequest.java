package com.hikari.rent.rental.dto.admin;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class AdminRentalCreateRequest {

  @NotBlank
  @Size(max = 150)
  private String name;

  @NotBlank
  @Pattern(regexp = "^\\d{5}$")
  private String areaCode;

  @Size(max = 10)
  private String postalCode;

  @NotBlank
  @Size(max = 255)
  private String address;

  @Size(max = 10000)
  private String description;

  @NotNull
  @Min(1)
  @Max(3)
  private Integer propertyType;

  @NotNull
  @Min(1)
  @Max(4294967295L)
  private Long monthlyRent;

  @Min(0)
  @Max(4294967295L)
  private Long managementFee;

  @DecimalMin("0")
  @Digits(integer = 3, fraction = 2)
  private BigDecimal depositMonths;

  @DecimalMin("0")
  @Digits(integer = 3, fraction = 2)
  private BigDecimal keyMoneyMonths;

  @Min(0)
  @Max(4294967295L)
  private Long brokerageFee;

  @Min(0)
  @Max(4294967295L)
  private Long guaranteeFee;

  @Min(0)
  @Max(4294967295L)
  private Long renewalFee;

  @NotBlank
  @Size(max = 20)
  private String floorPlan;

  @NotNull
  @DecimalMin(value = "0", inclusive = false)
  @Digits(integer = 6, fraction = 2)
  private BigDecimal area;

  @Min(-32768)
  @Max(32767)
  private Integer floor;

  @Min(1)
  @Max(65535)
  private Integer totalFloors;

  @Min(1800)
  @Max(2200)
  private Integer builtYear;

  @Min(1)
  @Max(12)
  private Integer builtMonth;

  @Size(max = 20)
  private String direction;

  @Size(max = 50)
  private String buildingStructure;

  private LocalDate availableFrom;

  @Size(max = 100)
  private String availableFromText;

  @Size(max = 30)
  private String contractType;

  @Min(1)
  @Max(65535)
  private Integer contractMonths;

  @Size(max = 150)
  private String guaranteeCompany;

  @DecimalMin("-90")
  @DecimalMax("90")
  @Digits(integer = 3, fraction = 7)
  private BigDecimal latitude;

  @DecimalMin("-180")
  @DecimalMax("180")
  @Digits(integer = 3, fraction = 7)
  private BigDecimal longitude;

  @Positive
  private Long agentId;
  @Positive
  private Long companyId;
  @Positive
  private Long managementCompanyId;

  @NotNull
  @Size(max = 10)
  @Valid
  private List<@NotNull Station> stations =
    new ArrayList<>();

  @NotNull
  @Size(max = 100)
  private List<
    @NotBlank @Size(max = 64) String
    > featureCodes = new ArrayList<>();

  @NotNull
  @Size(max = 21)
  @Valid
  private List<@NotNull Image> images;

  public record Image(
    @NotNull
    @Positive
    Long fileId,

    @NotBlank
    @Pattern(regexp = "PHOTO|FLOOR_PLAN")
    String kind,

    @NotNull
    @Min(0)
    Integer sortOrder
  ) {
  }

  public record Station(
    @NotNull @Positive Long stationId,

    @NotNull
    @Min(1)
    @Max(65535)
    Integer walkingMinutes
  ) {
  }
}
