package com.hikari.rent.rental.repository.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RentalImageRow {

  private Long rentalId;

  private Long fileId;

  private String storageKey;

  // PHOTO 或 FLOOR_PLAN。
  private String kind;

  private Integer sortOrder;
}
