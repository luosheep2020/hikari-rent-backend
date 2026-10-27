package com.hikari.rent.message.service.impl;

import java.nio.charset.StandardCharsets;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.List;

import com.hikari.rent.auth.service.CurrentUserService;

import com.hikari.rent.common.enums.ErrorCode;
import com.hikari.rent.common.exception.BusinessException;
import com.hikari.rent.common.result.CursorResult;

import com.hikari.rent.file.service.FileUrlService;
import com.hikari.rent.message.dto.MessageSessionPageRequest;
import com.hikari.rent.message.dto.OpenMessageSessionRequest;
import com.hikari.rent.message.repository.MessageSessionRepository;
import com.hikari.rent.message.repository.model.MessageSessionRow;
import com.hikari.rent.message.service.MessageSessionService;
import com.hikari.rent.message.vo.MessageSessionCreatedVO;
import com.hikari.rent.message.vo.MessageSessionVO;

import jakarta.annotation.Resource;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MessageSessionServiceImpl
  implements MessageSessionService {

  @Resource
  private CurrentUserService currentUserService;

  @Resource
  private MessageSessionRepository messageSessionRepository;

  @Resource
  private FileUrlService fileUrlService;

  @Override
  @Transactional
  @PreAuthorize("hasAuthority('conversation:read')")
  public MessageSessionCreatedVO openSession(
    OpenMessageSessionRequest request
  ) {

    Long userId = currentUserService.requireUserId();

    Long agentId =
      messageSessionRepository.findPublicRentalAgentId(
        request.rentalId()
      );

    if (agentId == null) {
      throw new BusinessException(
        ErrorCode.RENTAL_NOT_FOUND
      );
    }

    if (userId.equals(agentId)) {
      throw new BusinessException(
        ErrorCode.BAD_REQUEST
      );
    }

    Long sessionId =
      messageSessionRepository.createOrUpdateSession(
        userId,
        agentId,
        request.rentalId()
      );

    return new MessageSessionCreatedVO(
      sessionId.toString()
    );
  }

  @Override
  @Transactional(readOnly = true)
  @PreAuthorize("hasAuthority('conversation:read')")
  public CursorResult<MessageSessionVO> getSessions(
    MessageSessionPageRequest request
  ) {

    Long userId = currentUserService.requireUserId();

    int limit = request.getLimit();

    if (limit < 1 || limit > 50) {
      throw new BusinessException(
        ErrorCode.BAD_REQUEST
      );
    }

    Cursor cursor = decodeCursor(request.getCursor());

    // Fetch one extra row to detect the next page.
    List<MessageSessionRow> rows =
      messageSessionRepository.findSessionPage(
        userId,
        cursor == null ? null : cursor.activityAt(),
        cursor == null ? null : cursor.id(),
        limit + 1
      );

    boolean hasMore = rows.size() > limit;

    List<MessageSessionRow> visibleRows = hasMore
      ? rows.subList(0, limit)
      : rows;

    List<MessageSessionVO> items = visibleRows.stream()
      .map(this::toVO)
      .toList();

    String nextCursor = hasMore
      ? encodeCursor(visibleRows.getLast())
      : null;

    return new CursorResult<>(
      items,
      nextCursor,
      hasMore
    );
  }

  private MessageSessionVO toVO(
    MessageSessionRow row
  ) {

    MessageSessionVO.Contact contact =
      new MessageSessionVO.Contact(
        row.getContactId().toString(),
        row.getContactName(),
        row.getContactCompany(),
        publicUrl(row.getAvatarStorageKey())
      );

    MessageSessionVO.Rental rental = null;

    if (row.getRentalId() != null) {
      rental = new MessageSessionVO.Rental(
        row.getRentalId().toString(),
        row.getRentalName(),
        publicUrl(row.getImageStorageKey()),
        row.getFloorPlan(),
        row.getArea(),
        row.getMonthlyRent()
      );
    }

    MessageSessionVO.Preview preview = null;

    if (row.getLastMessageId() != null) {
      preview = new MessageSessionVO.Preview(
        row.getLastMessageId().toString(),
        row.getLastSenderId().toString(),
        row.getLastMessageText(),
        zeroIfNull(row.getLastImageCount()),
        row.getLastMessageCreatedAt()
          .toInstant(ZoneOffset.UTC)
      );
    }

    return new MessageSessionVO(
      row.getSessionId().toString(),
      contact,
      rental,
      preview,
      zeroIfNull(row.getUnreadCount())
    );
  }

  private String publicUrl(String storageKey) {

    if (storageKey == null || storageKey.isBlank()) {
      return null;
    }

    return fileUrlService.getPublicUrl(storageKey);
  }

  private long zeroIfNull(Long value) {
    return value == null ? 0L : value;
  }

  private String encodeCursor(
    MessageSessionRow row
  ) {

    String value =
      row.getActivityAt() + "|" + row.getSessionId();

    return Base64.getUrlEncoder()
      .withoutPadding()
      .encodeToString(
        value.getBytes(StandardCharsets.UTF_8)
      );
  }

  private Cursor decodeCursor(String value) {

    if (value == null || value.isBlank()) {
      return null;
    }

    if (value.length() > 256) {
      throw new BusinessException(
        ErrorCode.BAD_REQUEST
      );
    }

    try {
      String decoded = new String(
        Base64.getUrlDecoder().decode(value),
        StandardCharsets.UTF_8
      );

      String[] parts = decoded.split("\\|", -1);

      if (parts.length != 2) {
        throw new IllegalArgumentException(
          "Invalid cursor"
        );
      }

      LocalDateTime activityAt =
        LocalDateTime.parse(parts[0]);

      long id = Long.parseLong(parts[1]);

      if (id <= 0) {
        throw new IllegalArgumentException(
          "Invalid cursor ID"
        );
      }

      return new Cursor(activityAt, id);

    } catch (
      IllegalArgumentException | DateTimeException exception
    ) {

      throw new BusinessException(
        ErrorCode.BAD_REQUEST
      );
    }
  }

  private record Cursor(
    LocalDateTime activityAt,
    Long id
  ) {
  }
}
