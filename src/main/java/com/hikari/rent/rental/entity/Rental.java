package com.hikari.rent.rental.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.hikari.rent.rental.enums.RentalReviewStatus;
import com.hikari.rent.rental.enums.RentalStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@TableName("rentals")
public class Rental {

  @TableId(type = IdType.AUTO)
  private Long id;

  private String name;

  private String areaCode;

  private String postalCode;

  private String address;

  private String description;

  // 金额单位：日元。
  private Long monthlyRent;

  // null 表示未知，0 表示免收。
  private Long managementFee;

  private BigDecimal depositMonths;

  private BigDecimal keyMoneyMonths;

  private Long brokerageFee;

  private Long guaranteeFee;

  private Long renewalFee;

  private String floorPlan;

  private BigDecimal areaSqm;

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

  // 发布主体公司。
  private Long companyId;

  // 担当者用户。
  private Long agentId;

  // 管理公司。
  private Long managementCompanyId;

  private RentalStatus status;

  private RentalReviewStatus reviewStatus;

  private LocalDateTime submittedAt;

  private Long reviewedBy;

  private LocalDateTime reviewedAt;

  private String reviewRemark;

  private LocalDateTime publishedAt;

  private Long createdBy;

  private Long updatedBy;

  @Version
  private Integer version;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;
}
