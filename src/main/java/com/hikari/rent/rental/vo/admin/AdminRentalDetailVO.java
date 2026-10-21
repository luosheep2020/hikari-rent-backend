package com.hikari.rent.rental.vo.admin;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class AdminRentalDetailVO extends AdminRentalListVO {

  private String companyId;

  private String managementCompanyId;

  private String postalCode;

  private String description;

  private BigDecimal depositMonths;

  private BigDecimal keyMoneyMonths;

  private Long brokerageFee;

  private Long guaranteeFee;

  private Long renewalFee;

  private Integer floor;

  private Integer totalFloors;

  private Integer builtYear;

  private Integer builtMonth;

  private String direction;

  private String buildingStructure;

  private LocalDate availableFrom;

  private String availableFromText;

  private String contractType;

  private Integer contractMonths;

  private String guaranteeCompany;

  private BigDecimal latitude;

  private BigDecimal longitude;

  private LocalDateTime submittedAt;

  private String reviewedBy;

  private LocalDateTime reviewedAt;

  private String reviewRemark;

  private Long version;

  private List<Image> images = List.of();

  private List<Station> stations = List.of();

  private List<Feature> features = List.of();

  public record Image(
    String fileId,
    String kind,
    String url,
    Integer sortOrder
  ) {
  }

  public record Station(
    String stationId,
    String name,
    String line,
    Integer walkingMinutes
  ) {
  }

  public record Feature(
    String code,
    String name,
    String category
  ) {
  }
}
