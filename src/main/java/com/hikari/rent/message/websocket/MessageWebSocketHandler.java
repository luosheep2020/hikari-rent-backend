package com.hikari.rent.message.websocket;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@Component
@Profile("dev & !prod")
public class MessageWebSocketHandler extends TextWebSocketHandler {

  @Override
  public void afterConnectionEstablished(
    WebSocketSession session
  ) throws IOException {

    log.info(
      "Message WebSocket connected: sessionId={}",
      session.getId()
    );

    session.sendMessage(
      new TextMessage("接続しました")
    );
  }

  @Override
  protected void handleTextMessage(
    WebSocketSession session,
    TextMessage message
  ) throws IOException {

    String text = message.getPayload().trim();

    if (text.isEmpty()) {
      session.sendMessage(
        new TextMessage("メッセージを入力してください")
      );
      return;
    }

    if (text.length() > 500) {
      session.sendMessage(
        new TextMessage("500文字以内で入力してください")
      );
      return;
    }

    // Echo the message to verify the connection.
    session.sendMessage(
      new TextMessage("受信しました：" + text)
    );
  }

  @Override
  public void afterConnectionClosed(
    WebSocketSession session,
    CloseStatus status
  ) {

    log.info(
      "Message WebSocket disconnected: sessionId={}, code={}",
      session.getId(),
      status.getCode()
    );
  }

  @Override
  public void handleTransportError(
    WebSocketSession session,
    Throwable exception
  ) throws IOException {

    log.warn(
      "Message WebSocket transport failed: sessionId={}",
      session.getId(),
      exception
    );

    if (session.isOpen()) {
      session.close(CloseStatus.SERVER_ERROR);
    }
  }
}
