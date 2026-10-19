package com.hikari.rent.message.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import com.hikari.rent.auth.service.CurrentUserService;

import com.hikari.rent.common.enums.ErrorCode;
import com.hikari.rent.common.exception.BusinessException;
import com.hikari.rent.common.result.CursorResult;

import com.hikari.rent.message.dto.MarkMessageReadRequest;
import com.hikari.rent.message.dto.MessageRecordPageRequest;
import com.hikari.rent.message.dto.SendMessageRequest;
import com.hikari.rent.message.mapper.MessageRecordMapper;
import com.hikari.rent.message.repository.MessageRecordRepository;
import com.hikari.rent.message.repository.model.MessageRecordRow;
import com.hikari.rent.message.service.MessageRecordService;
import com.hikari.rent.message.vo.MessageRecordVO;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MessageRecordServiceImpl
  implements MessageRecordService {

  @Resource
  private CurrentUserService currentUserService;
  @Resource
  private MessageRecordMapper messageRecordMapper;
  @Resource
  private MessageRecordRepository messageRecordRepository;

  @Override
  @Transactional(readOnly = true)
  public CursorResult<MessageRecordVO> getMessages(
    Long sessionId,
    MessageRecordPageRequest request
  ) {

    validatePositiveId(sessionId);

    Long userId = currentUserService.requireUserId();

    requireSessionAccess(sessionId, userId);

    int limit = request.getLimit();

    if (limit < 1 || limit > 50) {
      throw new BusinessException(
        ErrorCode.BAD_REQUEST
      );
    }

    if (request.getBeforeId() != null) {
      validatePositiveId(request.getBeforeId());
    }

    List<MessageRecordRow> rows =
      messageRecordRepository.findMessagePage(
        sessionId,
        userId,
        request.getBeforeId(),
        limit + 1
      );

    boolean hasMore = rows.size() > limit;

    List<MessageRecordRow> visibleRows =
      new ArrayList<>(
        hasMore ? rows.subList(0, limit) : rows
      );

    // The database page is ordered from newest to oldest.
    String nextCursor = hasMore
      ? visibleRows.getLast().getId().toString()
      : null;

    // The frontend displays each page from oldest to newest.
    Collections.reverse(visibleRows);

    List<MessageRecordVO> items = visibleRows.stream()
      .map(this::toVO)
      .toList();

    return new CursorResult<>(
      items,
      nextCursor,
      hasMore
    );
  }

  @Override
  @Transactional
  public void markAsRead(
    Long sessionId,
    MarkMessageReadRequest request
  ) {

    validatePositiveId(sessionId);
    validatePositiveId(request.lastReadMessageId());

    Long userId = currentUserService.requireUserId();

    requireSessionAccess(sessionId, userId);

    if (
      !messageRecordRepository.messageExists(
        sessionId,
        request.lastReadMessageId()
      )
    ) {
      throw new BusinessException(
        ErrorCode.MESSAGE_NOT_FOUND
      );
    }

    messageRecordRepository.updateReadPosition(
      sessionId,
      userId,
      request.lastReadMessageId()
    );
  }

  private MessageRecordVO toVO(
    MessageRecordRow row
  ) {

    return new MessageRecordVO(
      row.getId().toString(),
      row.getSessionId().toString(),
      row.getSenderId().toString(),
      row.getClientMessageId(),
      row.getText(),
      List.of(),
      row.getCreatedAt().toInstant(ZoneOffset.UTC)
    );
  }

  private void requireSessionAccess(
    Long sessionId,
    Long userId
  ) {

    if (
      !messageRecordRepository.canAccessSession(
        sessionId,
        userId
      )
    ) {
      // Do not reveal whether another user's session exists.
      throw new BusinessException(
        ErrorCode.MESSAGE_SESSION_NOT_FOUND
      );
    }
  }

  private void validatePositiveId(Long id) {

    if (id == null || id <= 0) {
      throw new BusinessException(
        ErrorCode.BAD_REQUEST
      );
    }
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

  @Override
  @Transactional
  public MessageRecordVO sendMessage(
    Long sessionId,
    SendMessageRequest request
  ) {
    validatePositiveId(sessionId);

    Long userId = currentUserService.requireUserId();

    if (
      request == null
        || request.clientMessageId() == null
        || request.text() == null
        || request.attachmentIds() == null
    ) {
      throw new BusinessException(ErrorCode.BAD_REQUEST);
    }

    String clientMessageId = normalizeClientMessageId(
      request.clientMessageId()
    );

    String text = request.text().trim();

    if (request.text().length() > 500) {
      throw new BusinessException(ErrorCode.BAD_REQUEST);
    }

    if (!request.attachmentIds().isEmpty()) {
      throw new BusinessException(
        ErrorCode.MESSAGE_ATTACHMENTS_NOT_READY
      );
    }

    if (text.isBlank()) {
      throw new BusinessException(ErrorCode.BAD_REQUEST);
    }

    boolean accessible = messageRecordRepository
      .lockAccessibleSession(sessionId, userId);

    if (!accessible) {
      throw new BusinessException(
        ErrorCode.MESSAGE_SESSION_NOT_FOUND
      );
    }

    messageRecordRepository.insertOrReuseMessage(
      sessionId,
      userId,
      clientMessageId,
      text
    );

    MessageRecordRow saved = messageRecordRepository
      .findByClientMessageId(
        userId,
        clientMessageId
      );

    if (saved == null) {
      throw new IllegalStateException(
        "Persisted message could not be found"
      );
    }

    if (
      !sessionId.equals(saved.getSessionId())
        || !text.equals(saved.getText())
    ) {
      throw new BusinessException(
        ErrorCode.MESSAGE_IDEMPOTENCY_CONFLICT
      );
    }

    messageRecordRepository.updateLatestMessage(
      sessionId,
      saved.getId(),
      saved.getCreatedAt()
    );

    return toVO(saved);
  }

  private String normalizeClientMessageId(String value) {
    try {
      String normalized = UUID.fromString(value).toString();

      if (!normalized.equalsIgnoreCase(value)) {
        throw new BusinessException(
          ErrorCode.BAD_REQUEST
        );
      }

      return normalized;
    } catch (IllegalArgumentException exception) {
      throw new BusinessException(
        ErrorCode.BAD_REQUEST
      );
    }
  }
}
