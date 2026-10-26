package com.hikari.rent.company.vo;

import java.time.LocalDateTime;

public record AdminCompanyVO(
  String id,
  String name,
  String postalCode,
  String address,
  String phone,
  String email,
  String licenseNumber,
  String businessHours,
  String closedDays,
  Integer status,
  LocalDateTime createdAt,
  LocalDateTime updatedAt
) {
}
