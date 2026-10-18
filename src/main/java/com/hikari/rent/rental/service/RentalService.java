package com.hikari.rent.rental.service;

import com.hikari.rent.common.result.PageResult;
import com.hikari.rent.rental.dto.RentalMapRequest;
import com.hikari.rent.rental.dto.RentalPageRequest;
import com.hikari.rent.rental.vo.RentalDetailVO;
import com.hikari.rent.rental.vo.RentalFeatureOptionVO;
import com.hikari.rent.rental.vo.RentalListVO;
import com.hikari.rent.rental.vo.RentalMapVO;

import java.util.List;

public interface RentalService {

  PageResult<RentalListVO> searchPage(
    RentalPageRequest request
  );

  RentalDetailVO getDetail(Long id);

  List<RentalFeatureOptionVO> getFeatureOptions();


  RentalMapVO searchMap(RentalMapRequest request);

  List<RentalListVO> getPublicListByIds(
    List<Long> rentalIds
  );
}
