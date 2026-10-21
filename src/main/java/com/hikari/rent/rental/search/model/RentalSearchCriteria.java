package com.hikari.rent.rental.search.model;

public record RentalSearchCriteria(
  int pageNum,
  int pageSize,
  String keyword,
  String areaCode,
  Long minRent,
  Long maxRent,
  String sort
) {

  public RentalSearchCriteria {
    if (pageNum < 1) {
      throw new IllegalArgumentException(
        "pageNum must be greater than zero"
      );
    }

    if (pageSize < 1 || pageSize > 100) {
      throw new IllegalArgumentException(
        "pageSize must be between 1 and 100"
      );
    }

    if (minRent != null && minRent < 0) {
      throw new IllegalArgumentException(
        "minRent must not be negative"
      );
    }

    if (maxRent != null && maxRent < 0) {
      throw new IllegalArgumentException(
        "maxRent must not be negative"
      );
    }

    if (
      minRent != null
        && maxRent != null
        && minRent > maxRent
    ) {
      throw new IllegalArgumentException(
        "minRent must not exceed maxRent"
      );
    }

    keyword = normalize(keyword);
    areaCode = normalize(areaCode);
    sort = normalize(sort);

    if (sort == null) {
      sort = "recommended";
    }

    if (
      !sort.equals("recommended")
        && !sort.equals("rentAsc")
        && !sort.equals("rentDesc")
        && !sort.equals("areaDesc")
    ) {
      throw new IllegalArgumentException(
        "Unsupported rental sort"
      );
    }
  }

  private static String normalize(String value) {
    if (value == null) {
      return null;
    }

    String normalized = value.strip();

    return normalized.isEmpty()
      ? null
      : normalized;
  }
}
