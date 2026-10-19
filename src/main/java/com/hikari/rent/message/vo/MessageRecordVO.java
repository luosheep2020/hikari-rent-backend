package com.hikari.rent.message.vo;

import java.time.Instant;
import java.util.List;

public record MessageRecordVO(
  String id,
  String sessionId,
  String senderId,
  String clientMessageId,
  String text,
  List<Image> images,
  Instant createdAt
) {

  public record Image(
    String id,
    String url,
    String name
  ) {
  }
}
