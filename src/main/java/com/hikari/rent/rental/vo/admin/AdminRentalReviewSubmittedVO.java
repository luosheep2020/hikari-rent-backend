package com.hikari.rent.rental.vo.admin;

public record AdminRentalReviewSubmittedVO(
  String id,
  long version,
  int reviewStatus
) {
}
