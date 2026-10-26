package com.hikari.rent.company.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Company {

  private Long id;

  private String name;

  private String postalCode;

  private String address;

  private String phone;

  private String email;

  private String licenseNumber;

  private String businessHours;

  private String closedDays;

  private Integer status;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;
}
