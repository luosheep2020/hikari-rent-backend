package com.hikari.rent.railway.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AdminStationVO(
  String id,
  String sourceCode,
  String stationGroupCode,
  String name,
  String nameKana,
  String lineId,
  String lineName,
  String operatorSourceCode,
  String operatorName,
  String prefectureCode,
  String prefectureName,
  String areaCode,
  String areaName,
  String address,
  String postalCode,
  BigDecimal latitude,
  BigDecimal longitude,
  Integer status,
  Integer sortOrder,
  LocalDateTime createdAt,
  LocalDateTime updatedAt
) {
}
