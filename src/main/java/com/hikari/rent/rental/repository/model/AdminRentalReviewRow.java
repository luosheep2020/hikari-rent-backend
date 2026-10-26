package com.hikari.rent.rental.repository.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminRentalReviewRow {

  private Long id;

  private Integer status;

  private Integer reviewStatus;

  private Long version;
}
