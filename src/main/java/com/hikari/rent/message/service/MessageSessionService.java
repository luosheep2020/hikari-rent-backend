package com.hikari.rent.message.service;

import com.hikari.rent.common.result.CursorResult;

import com.hikari.rent.message.dto.MessageSessionPageRequest;
import com.hikari.rent.message.dto.OpenMessageSessionRequest;
import com.hikari.rent.message.vo.MessageSessionCreatedVO;
import com.hikari.rent.message.vo.MessageSessionVO;

public interface MessageSessionService {

  MessageSessionCreatedVO openSession(
    OpenMessageSessionRequest request
  );

  CursorResult<MessageSessionVO> getSessions(
    MessageSessionPageRequest request
  );
}
