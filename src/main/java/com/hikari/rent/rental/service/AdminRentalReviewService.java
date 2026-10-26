package com.hikari.rent.rental.service;

import com.hikari.rent.rental.dto.admin.AdminRentalReviewRequest;
import com.hikari.rent.rental.vo.admin.AdminRentalReviewedVO;

public interface AdminRentalReviewService {

  AdminRentalReviewedVO review(
    Long rentalId,
    AdminRentalReviewRequest request
  );
}
