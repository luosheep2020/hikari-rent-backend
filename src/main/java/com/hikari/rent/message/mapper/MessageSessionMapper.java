package com.hikari.rent.message.mapper;

import java.time.LocalDateTime;
import java.util.List;

import com.hikari.rent.message.repository.model.MessageSessionRow;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MessageSessionMapper {

  Long selectPublicRentalAgentId(
    @Param("rentalId") Long rentalId
  );

  int upsertSession(
    @Param("customerId") Long customerId,
    @Param("agentId") Long agentId,
    @Param("rentalId") Long rentalId
  );

  Long selectSessionId(
    @Param("customerId") Long customerId,
    @Param("agentId") Long agentId
  );

  List<MessageSessionRow> selectSessionPage(
    @Param("userId") Long userId,
    @Param("beforeTime") LocalDateTime beforeTime,
    @Param("beforeId") Long beforeId,
    @Param("limit") int limit
  );
}
