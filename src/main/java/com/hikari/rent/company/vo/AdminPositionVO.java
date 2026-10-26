package com.hikari.rent.company.vo;

import java.time.LocalDateTime;

public record AdminPositionVO(
  String id,
  String companyId,
  String name,
  String code,
  Integer sortOrder,
  Integer status,
  LocalDateTime createdAt,
  LocalDateTime updatedAt
) {
}
