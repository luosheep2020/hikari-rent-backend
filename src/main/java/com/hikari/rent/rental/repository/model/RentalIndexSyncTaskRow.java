package com.hikari.rent.rental.repository.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RentalIndexSyncTaskRow {

  private Long rentalId;

  private Integer retryCount;
}
