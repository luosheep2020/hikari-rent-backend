package com.hikari.rent.rental.service;

import com.hikari.rent.common.result.PageResult;
import com.hikari.rent.rental.dto.admin.AdminRentalCreateRequest;
import com.hikari.rent.rental.dto.admin.AdminRentalPageRequest;
import com.hikari.rent.rental.dto.admin.AdminRentalSubmitReviewRequest;
import com.hikari.rent.rental.dto.admin.AdminRentalUpdateRequest;
import com.hikari.rent.rental.vo.admin.*;

public interface AdminRentalService {

  PageResult<AdminRentalListVO> searchPage(
    AdminRentalPageRequest request
  );

  AdminRentalDetailVO getDetail(Long rentalId);

  AdminRentalCreatedVO createDraft(
    AdminRentalCreateRequest request
  );

  AdminRentalUpdatedVO updateDraft(
    Long rentalId,
    AdminRentalUpdateRequest request
  );

  AdminRentalReviewSubmittedVO submitReview(
    Long rentalId,
    AdminRentalSubmitReviewRequest request
  );


}
