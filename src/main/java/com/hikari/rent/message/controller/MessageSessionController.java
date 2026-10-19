package com.hikari.rent.message.controller;

import com.hikari.rent.common.result.ApiResponse;
import com.hikari.rent.common.result.CursorResult;

import com.hikari.rent.message.dto.MessageSessionPageRequest;
import com.hikari.rent.message.dto.OpenMessageSessionRequest;
import com.hikari.rent.message.service.MessageSessionService;
import com.hikari.rent.message.vo.MessageSessionCreatedVO;
import com.hikari.rent.message.vo.MessageSessionVO;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/messages/sessions")
public class MessageSessionController {

  @Resource
  private MessageSessionService messageSessionService;

  @GetMapping
  public ApiResponse<CursorResult<MessageSessionVO>> getSessions(
    @Valid @ModelAttribute MessageSessionPageRequest request
  ) {

    return ApiResponse.success(
      messageSessionService.getSessions(request)
    );
  }

  @PostMapping
  public ApiResponse<MessageSessionCreatedVO> openSession(
    @Valid @RequestBody OpenMessageSessionRequest request
  ) {

    return ApiResponse.success(
      messageSessionService.openSession(request)
    );
  }
}
