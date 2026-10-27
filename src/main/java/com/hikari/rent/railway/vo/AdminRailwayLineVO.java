package com.hikari.rent.railway.vo;

import java.time.LocalDateTime;

public record AdminRailwayLineVO(
  String id,
  String sourceCode,
  String name,
  String nameKana,
  String operatorSourceCode,
  String operatorName,
  String color,
  Integer status,
  Integer sortOrder,
  LocalDateTime createdAt,
  LocalDateTime updatedAt
) {
}
