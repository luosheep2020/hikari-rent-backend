package com.hikari.rent.message.repository;

import java.time.LocalDateTime;
import java.util.List;

import com.hikari.rent.message.repository.model.MessageRecordRow;

public interface MessageRecordRepository {

  boolean canAccessSession(
    Long sessionId,
    Long userId
  );

  List<MessageRecordRow> findMessagePage(
    Long sessionId,
    Long userId,
    Long beforeId,
    int limit
  );

  boolean messageExists(
    Long sessionId,
    Long messageId
  );

  void updateReadPosition(
    Long sessionId,
    Long userId,
    Long messageId
  );

  boolean lockAccessibleSession(
    Long sessionId,
    Long userId
  );

  void insertOrReuseMessage(
    Long sessionId,
    Long userId,
    String clientMessageId,
    String text
  );

  MessageRecordRow findByClientMessageId(
    Long userId,
    String clientMessageId
  );

  void updateLatestMessage(
    Long sessionId,
    Long messageId,
    LocalDateTime createdAt
  );
}
