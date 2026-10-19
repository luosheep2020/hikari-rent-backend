package com.hikari.rent.message.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageRecordPageRequest {

  @Positive(message = "Before ID must be positive")
  private Long beforeId;

  @Min(value = 1, message = "Limit must be at least 1")
  @Max(value = 50, message = "Limit must not exceed 50")
  private int limit = 30;
}
