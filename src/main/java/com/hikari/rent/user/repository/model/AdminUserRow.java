package com.hikari.rent.user.repository.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AdminUserRow {

  private Long id;

  private String email;

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
