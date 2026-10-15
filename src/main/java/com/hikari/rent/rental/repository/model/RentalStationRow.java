package com.hikari.rent.rental.repository.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RentalStationRow {

  private Long rentalId;

  private Long stationId;

  private String name;

  private String lineName;

  private Integer walkingMinutes;
}
