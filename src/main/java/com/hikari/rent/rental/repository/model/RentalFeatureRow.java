package com.hikari.rent.rental.repository.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RentalFeatureRow {

  private Long rentalId;

  private Long featureId;

  private String code;

  private String name;

  // AMENITY 或 CONDITION。
  private String category;
}
