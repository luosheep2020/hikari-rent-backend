package com.hikari.rent.rental.search.event;

public record RentalIndexChangedEvent(
  Long rentalId
) {

  public RentalIndexChangedEvent {
    if (rentalId == null || rentalId <= 0) {
      throw new IllegalArgumentException(
        "rentalId must be greater than zero"
      );
    }
  }
}
