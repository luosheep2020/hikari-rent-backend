package com.hikari.rent.rental.search.service;

import com.hikari.rent.rental.search.model.RentalIndexImportResult;

public interface RentalIndexService {

  RentalIndexImportResult importPublicRentals();

  void syncRental(Long rentalId);
}
