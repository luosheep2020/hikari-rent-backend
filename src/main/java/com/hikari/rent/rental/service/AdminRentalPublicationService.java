package com.hikari.rent.rental.service;

import com.hikari.rent.rental.dto.admin.AdminRentalPublicationRequest;
import com.hikari.rent.rental.vo.admin.AdminRentalPublicationVO;

public interface AdminRentalPublicationService {

  AdminRentalPublicationVO publish(
    Long rentalId,
    AdminRentalPublicationRequest request
  );

  AdminRentalPublicationVO unpublish(
    Long rentalId,
    AdminRentalPublicationRequest request
  );

  AdminRentalPublicationVO markRented(
    Long rentalId,
    AdminRentalPublicationRequest request
  );
}
