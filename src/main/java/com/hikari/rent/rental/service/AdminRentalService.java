package com.hikari.rent.rental.service;

import com.hikari.rent.common.result.PageResult;
import com.hikari.rent.rental.dto.admin.AdminRentalCreateRequest;
import com.hikari.rent.rental.dto.admin.AdminRentalPageRequest;
import com.hikari.rent.rental.dto.admin.AdminRentalUpdateRequest;
import com.hikari.rent.rental.vo.admin.AdminRentalCreatedVO;
import com.hikari.rent.rental.vo.admin.AdminRentalDetailVO;
import com.hikari.rent.rental.vo.admin.AdminRentalListVO;
import com.hikari.rent.rental.vo.admin.AdminRentalUpdatedVO;

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
}
