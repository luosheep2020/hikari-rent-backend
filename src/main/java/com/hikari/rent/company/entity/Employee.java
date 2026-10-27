package com.hikari.rent.company.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Employee {

  private Long id;

  private String email;

  private String passwordHash;

  private String nickname;

  private String phone;

  private Long companyId;

  private String companyName;

  private Long departmentId;

  private String departmentName;

  private Long positionId;

  private String positionName;

  private Integer status;

  private LocalDateTime lastLoginAt;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;
}
