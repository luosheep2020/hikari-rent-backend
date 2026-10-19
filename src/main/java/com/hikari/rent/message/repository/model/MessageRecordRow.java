package com.hikari.rent.message.repository.model;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageRecordRow {

  private Long id;

  private Long sessionId;

  private Long senderId;

  private String clientMessageId;

  private String text;

  private LocalDateTime createdAt;
}
