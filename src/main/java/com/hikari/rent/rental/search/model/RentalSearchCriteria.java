package com.hikari.rent.rental.search.model;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public record RentalSearchCriteria(
  int pageNum,
  int pageSize,
  String keyword,
  String areaCode,
  Long minRent,
  Long maxRent,
  Integer propertyType,
  List<String> floorPlans,
  Integer maxBuildingAge,
  Long stationId,
  Integer maxWalkingMinutes,
  List<String> featureCodes,
  String sort
) {

  private static final Set<String> SUPPORTED_SORTS =
    Set.of(
      "recommended",
      "rentAsc",
      "rentDesc",
      "areaDesc",
      "walkingAsc"
    );

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

    if (
      propertyType != null
        && (propertyType < 1 || propertyType > 3)
    ) {
      throw new IllegalArgumentException(
        "propertyType must be between 1 and 3"
      );
    }

    if (
      maxBuildingAge != null
        && (maxBuildingAge < 0 || maxBuildingAge > 200)
    ) {
      throw new IllegalArgumentException(
        "maxBuildingAge must be between 0 and 200"
      );
    }

    if (stationId != null && stationId <= 0) {
      throw new IllegalArgumentException(
        "stationId must be greater than zero"
      );
    }

    if (
      maxWalkingMinutes != null
        && maxWalkingMinutes < 0
    ) {
      throw new IllegalArgumentException(
        "maxWalkingMinutes must not be negative"
      );
    }

    keyword = normalize(keyword);
    areaCode = normalize(areaCode);
    floorPlans = normalizeList(floorPlans);
    featureCodes = normalizeList(featureCodes);
    sort = normalize(sort);

    if (sort == null) {
      sort = "recommended";
    }

    if (!SUPPORTED_SORTS.contains(sort)) {
      throw new IllegalArgumentException(
        "Unsupported rental sort"
      );
    }
  }

  // Preserve compatibility with the earlier search example.
  public RentalSearchCriteria(
    int pageNum,
    int pageSize,
    String keyword,
    String areaCode,
    Long minRent,
    Long maxRent,
    String sort
  ) {
    this(
      pageNum,
      pageSize,
      keyword,
      areaCode,
      minRent,
      maxRent,
      null,
      List.of(),
      null,
      null,
      null,
      List.of(),
      sort
    );
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

  private static List<String> normalizeList(
    List<String> values
  ) {
    if (values == null) {
      return List.of();
    }

    return values.stream()
      .filter(Objects::nonNull)
      .map(String::strip)
      .filter(value -> !value.isEmpty())
      .distinct()
      .toList();
  }
}
