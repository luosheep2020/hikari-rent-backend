package com.hikari.rent.rental.dto.admin;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminRentalUpdateRequest
  extends AdminRentalCreateRequest {

  @NotNull
  @Min(0)
  @Max(4294967294L)
  private Long version;
}
