package com.hikari.rent.message.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageSessionPageRequest {

  @Size(
    max = 256,
    message = "Cursor must not exceed 256 characters"
  )
  private String cursor;

  @Min(
    value = 1,
    message = "Limit must be at least 1"
  )
  @Max(
    value = 50,
    message = "Limit must not exceed 50"
  )
  private int limit = 20;
}
