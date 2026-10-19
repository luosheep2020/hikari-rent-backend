package com.hikari.rent.message.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

public record SendMessageRequest(

  @NotBlank(message = "Client message ID is required")
  @Pattern(
    regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
    message = "Client message ID must be a UUID"
  )
  String clientMessageId,

  @NotNull(message = "Message text is required")
  @Size(
    max = 500,
    message = "Message text must not exceed 500 characters"
  )
  String text,

  @NotNull(message = "Attachment IDs are required")
  @Size(
    max = 4,
    message = "A message can contain at most 4 attachments"
  )
  List<@NotNull @Positive Long> attachmentIds

) {
}
