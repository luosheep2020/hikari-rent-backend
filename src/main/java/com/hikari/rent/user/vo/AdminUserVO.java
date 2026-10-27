package com.hikari.rent.user.vo;

import java.time.LocalDateTime;
import java.util.List;

public record AdminUserVO(
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
  List<String> roles,
  LocalDateTime lastLoginAt,
  LocalDateTime createdAt,
  LocalDateTime updatedAt
) {
}
