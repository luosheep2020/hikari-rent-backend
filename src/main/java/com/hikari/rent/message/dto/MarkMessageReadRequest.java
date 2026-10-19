package com.hikari.rent.message.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record MarkMessageReadRequest(

  @NotNull(message = "Last read message ID is required")
  @Positive(message = "Last read message ID must be positive")
  Long lastReadMessageId

) {
}
