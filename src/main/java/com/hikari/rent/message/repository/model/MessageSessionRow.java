package com.hikari.rent.message.repository.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageSessionRow {

  private Long sessionId;

  private LocalDateTime activityAt;

  private Long contactId;

  private String contactName;

  private String contactCompany;

  private String avatarStorageKey;

  private Long rentalId;

  private String rentalName;

  private String imageStorageKey;

  private String floorPlan;

  private BigDecimal area;

  private Long monthlyRent;

  private Long lastMessageId;

  private Long lastSenderId;

  private String lastMessageText;

  private Long lastImageCount;

  private LocalDateTime lastMessageCreatedAt;

  private Long unreadCount;
}
