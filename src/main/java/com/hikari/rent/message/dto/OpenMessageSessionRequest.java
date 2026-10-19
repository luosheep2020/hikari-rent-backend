package com.hikari.rent.message.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OpenMessageSessionRequest(

  @NotNull(message = "Rental ID is required")
  @Positive(message = "Rental ID must be positive")
  Long rentalId

) {
}
