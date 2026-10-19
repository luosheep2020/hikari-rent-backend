package com.hikari.rent.message.controller;

import com.hikari.rent.common.result.ApiResponse;
import com.hikari.rent.common.result.CursorResult;

import com.hikari.rent.message.dto.MarkMessageReadRequest;
import com.hikari.rent.message.dto.MessageRecordPageRequest;
import com.hikari.rent.message.service.MessageRecordService;
import com.hikari.rent.message.vo.MessageRecordVO;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/messages/sessions/{sessionId}")
public class MessageRecordController {

  @Resource
  private MessageRecordService messageRecordService;

  @GetMapping("/records")
  public ApiResponse<CursorResult<MessageRecordVO>> getMessages(
    @PathVariable("sessionId") Long sessionId,
    @Valid @ModelAttribute MessageRecordPageRequest request
  ) {

    return ApiResponse.success(
      messageRecordService.getMessages(
        sessionId,
        request
      )
    );
  }

  @PutMapping("/read")
  public ApiResponse<Void> markAsRead(
    @PathVariable("sessionId") Long sessionId,
    @Valid @RequestBody MarkMessageReadRequest request
  ) {

    messageRecordService.markAsRead(
      sessionId,
      request
    );

    return ApiResponse.success();
  }
}
