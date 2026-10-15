package com.hikari.rent.rental.exception;

public class RentalNotFoundException extends RuntimeException {

  public RentalNotFoundException() {
    super("Rental not found");
  }
}
