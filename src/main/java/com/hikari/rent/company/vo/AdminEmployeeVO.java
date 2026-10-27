package com.hikari.rent.company.vo;

import java.time.LocalDateTime;

public record AdminEmployeeVO(
  String id,
  String email,
  String nickname,
  String phone,
  String companyId,
  String companyName,
  String departmentId,
  String departmentName,
  String positionId,
  String positionName,
  Integer status,
  LocalDateTime lastLoginAt,
  LocalDateTime createdAt,
  LocalDateTime updatedAt
) {
}
