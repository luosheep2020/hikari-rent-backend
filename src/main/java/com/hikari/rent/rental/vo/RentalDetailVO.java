package com.hikari.rent.rental.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class RentalDetailVO extends RentalListVO {

  // 房源照片，不包含户型图；户型图沿用父类 floorPlanImage。
  private List<String> images = new ArrayList<>();

  // 其他费用，单位：日元。未知费用返回 null。
  private Long brokerageFee;

  private Long guaranteeFee;

  private Long renewalFee;

  // 建筑信息
  private String direction;

  private String buildingStructure;

  // 入住与合同信息
  private String availableFrom;

  private String contractType;

  private String contractPeriod;

  private String guaranteeCompany;

  // 房源介绍
  private String description;

  // 担当者
  private RentalAgentVO agent;

  // 管理会社
  private RentalManagementCompanyVO managementCompany;
}
