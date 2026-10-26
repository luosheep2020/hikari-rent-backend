package com.hikari.rent.rental.search.model;

public record RentalIndexImportResult(
  long processedCount,
  long lastRentalId,
  long upperRentalId
) {
}
