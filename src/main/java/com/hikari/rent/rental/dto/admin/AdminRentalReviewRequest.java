package com.hikari.rent.rental.dto.admin;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminRentalReviewRequest {

  @NotNull
  @PositiveOrZero
  @Max(2147483646L)
  private Long version;

  @NotNull
  private Boolean approved;

  @Size(max = 500)
  private String remark;
}
