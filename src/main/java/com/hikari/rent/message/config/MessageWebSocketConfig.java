package com.hikari.rent.message.config;

import com.hikari.rent.message.websocket.MessageWebSocketHandler;

import jakarta.annotation.Resource;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@Profile("dev & !prod")
public class MessageWebSocketConfig implements WebSocketConfigurer {

  @Resource
  private MessageWebSocketHandler messageWebSocketHandler;

  @Override
  public void registerWebSocketHandlers(
    WebSocketHandlerRegistry registry
  ) {

    registry
      .addHandler(
        messageWebSocketHandler,
        "/ws/messages"
      )
      .setAllowedOrigins(
        "http://localhost:3000",
        "http://127.0.0.1:3000"
      );
  }
}
