package com.hikari.rent.message.repository.impl;

import java.time.LocalDateTime;
import java.util.List;

import com.hikari.rent.message.mapper.MessageRecordMapper;
import com.hikari.rent.message.repository.MessageRecordRepository;
import com.hikari.rent.message.repository.model.MessageRecordRow;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Repository;

@Repository
public class MessageRecordRepositoryImpl
  implements MessageRecordRepository {

  @Resource
  private MessageRecordMapper messageRecordMapper;

  @Override
  public boolean canAccessSession(
    Long sessionId,
    Long userId
  ) {

    return messageRecordMapper.selectAccessibleSessionId(
      sessionId,
      userId
    ) != null;
  }

  @Override
  public List<MessageRecordRow> findMessagePage(
    Long sessionId,
    Long userId,
    Long beforeId,
    int limit
  ) {

    return messageRecordMapper.selectMessagePage(
      sessionId,
      userId,
      beforeId,
      limit
    );
  }

  @Override
  public boolean messageExists(
    Long sessionId,
    Long messageId
  ) {

    return messageRecordMapper.selectMessageId(
      sessionId,
      messageId
    ) != null;
  }

  @Override
  public void updateReadPosition(
    Long sessionId,
    Long userId,
    Long messageId
  ) {

    messageRecordMapper.updateReadPosition(
      sessionId,
      userId,
      messageId
    );
  }

  @Override
  public boolean lockAccessibleSession(
    Long sessionId,
    Long userId
  ) {
    return messageRecordMapper
      .selectAccessibleSessionIdForUpdate(
        sessionId,
        userId
      ) != null;
  }

  @Override
  public void insertOrReuseMessage(
    Long sessionId,
    Long userId,
    String clientMessageId,
    String text
  ) {
    messageRecordMapper.insertOrReuseMessage(
      sessionId,
      userId,
      clientMessageId,
      text
    );
  }

  @Override
  public MessageRecordRow findByClientMessageId(
    Long userId,
    String clientMessageId
  ) {
    return messageRecordMapper.selectMessageByClientId(
      userId,
      clientMessageId
    );
  }

  @Override
  public void updateLatestMessage(
    Long sessionId,
    Long messageId,
    LocalDateTime createdAt
  ) {
    messageRecordMapper.updateLatestMessage(
      sessionId,
      messageId,
      createdAt
    );
  }
}
