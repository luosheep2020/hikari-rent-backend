package com.hikari.rent.rental.dto.admin;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminRentalPublicationRequest {

  @NotNull
  @PositiveOrZero
  @Max(2147483646L)
  private Long version;
}
