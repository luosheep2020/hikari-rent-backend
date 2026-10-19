package com.hikari.rent.message.vo;

import java.math.BigDecimal;
import java.time.Instant;

public record MessageSessionVO(
  String id,
  Contact contact,
  Rental rental,
  Preview lastMessage,
  long unreadCount
) {

  public record Contact(
    String id,
    String name,
    String company,
    String avatar
  ) {
  }

  public record Rental(
    String id,
    String name,
    String image,
    String floorPlan,
    BigDecimal area,
    Long monthlyRent
  ) {
  }

  public record Preview(
    String id,
    String senderId,
    String text,
    long imageCount,
    Instant createdAt
  ) {
  }
}
