package com.hikari.rent.message.service;

import com.hikari.rent.common.result.CursorResult;

import com.hikari.rent.message.dto.MarkMessageReadRequest;
import com.hikari.rent.message.dto.MessageRecordPageRequest;
import com.hikari.rent.message.dto.SendMessageRequest;
import com.hikari.rent.message.repository.model.MessageRecordRow;
import com.hikari.rent.message.vo.MessageRecordVO;

import java.time.LocalDateTime;

public interface MessageRecordService {

  CursorResult<MessageRecordVO> getMessages(
    Long sessionId,
    MessageRecordPageRequest request
  );

  void markAsRead(
    Long sessionId,
    MarkMessageReadRequest request
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


  MessageRecordVO sendMessage(
    Long sessionId,
    SendMessageRequest request
  );
}
