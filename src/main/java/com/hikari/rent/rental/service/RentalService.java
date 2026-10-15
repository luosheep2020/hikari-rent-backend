package com.hikari.rent.rental.service;

import com.hikari.rent.common.result.PageResult;
import com.hikari.rent.rental.dto.RentalPageRequest;
import com.hikari.rent.rental.vo.RentalDetailVO;
import com.hikari.rent.rental.vo.RentalListVO;

public interface RentalService {

  PageResult<RentalListVO> searchPage(
    RentalPageRequest request
  );

  RentalDetailVO getDetail(Long id);
}
