package com.hikari.rent.message.repository;

import java.time.LocalDateTime;
import java.util.List;

import com.hikari.rent.message.repository.model.MessageSessionRow;

public interface MessageSessionRepository {

  Long findPublicRentalAgentId(Long rentalId);

  Long createOrUpdateSession(
    Long customerId,
    Long agentId,
    Long rentalId
  );

  List<MessageSessionRow> findSessionPage(
    Long userId,
    LocalDateTime beforeTime,
    Long beforeId,
    int limit
  );
}
