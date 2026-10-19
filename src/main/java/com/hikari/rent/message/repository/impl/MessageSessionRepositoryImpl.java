package com.hikari.rent.message.repository.impl;

import java.time.LocalDateTime;
import java.util.List;

import com.hikari.rent.message.mapper.MessageSessionMapper;
import com.hikari.rent.message.repository.MessageSessionRepository;
import com.hikari.rent.message.repository.model.MessageSessionRow;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Repository;

@Repository
public class MessageSessionRepositoryImpl
  implements MessageSessionRepository {

  @Resource
  private MessageSessionMapper messageSessionMapper;

  @Override
  public Long findPublicRentalAgentId(Long rentalId) {

    return messageSessionMapper.selectPublicRentalAgentId(
      rentalId
    );
  }

  @Override
  public Long createOrUpdateSession(
    Long customerId,
    Long agentId,
    Long rentalId
  ) {

    messageSessionMapper.upsertSession(
      customerId,
      agentId,
      rentalId
    );

    Long sessionId =
      messageSessionMapper.selectSessionId(
        customerId,
        agentId
      );

    if (sessionId == null) {
      throw new IllegalStateException(
        "Message session was not found after upsert"
      );
    }

    return sessionId;
  }

  @Override
  public List<MessageSessionRow> findSessionPage(
    Long userId,
    LocalDateTime beforeTime,
    Long beforeId,
    int limit
  ) {

    return messageSessionMapper.selectSessionPage(
      userId,
      beforeTime,
      beforeId,
      limit
    );
  }
}
