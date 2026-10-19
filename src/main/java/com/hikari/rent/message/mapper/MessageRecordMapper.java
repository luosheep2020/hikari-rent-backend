package com.hikari.rent.message.mapper;

import java.time.LocalDateTime;
import java.util.List;

import com.hikari.rent.message.repository.model.MessageRecordRow;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MessageRecordMapper {

  Long selectAccessibleSessionId(
    @Param("sessionId") Long sessionId,
    @Param("userId") Long userId
  );

  List<MessageRecordRow> selectMessagePage(
    @Param("sessionId") Long sessionId,
    @Param("userId") Long userId,
    @Param("beforeId") Long beforeId,
    @Param("limit") int limit
  );

  Long selectMessageId(
    @Param("sessionId") Long sessionId,
    @Param("messageId") Long messageId
  );

  int updateReadPosition(
    @Param("sessionId") Long sessionId,
    @Param("userId") Long userId,
    @Param("messageId") Long messageId
  );

  Long selectAccessibleSessionIdForUpdate(
    @Param("sessionId") Long sessionId,
    @Param("userId") Long userId
  );

  int insertOrReuseMessage(
    @Param("sessionId") Long sessionId,
    @Param("userId") Long userId,
    @Param("clientMessageId") String clientMessageId,
    @Param("text") String text
  );

  MessageRecordRow selectMessageByClientId(
    @Param("userId") Long userId,
    @Param("clientMessageId") String clientMessageId
  );

  int updateLatestMessage(
    @Param("sessionId") Long sessionId,
    @Param("messageId") Long messageId,
    @Param("createdAt") LocalDateTime createdAt
  );
}
