package com.hikari.rent.rental.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hikari.rent.rental.dto.RentalMapRequest;
import com.hikari.rent.rental.dto.RentalPageRequest;
import com.hikari.rent.rental.entity.Rental;
import com.hikari.rent.rental.repository.model.*;
import com.hikari.rent.rental.vo.RentalFeatureOptionVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RentalMapper extends BaseMapper<Rental> {

  IPage<Rental> searchPage(
    IPage<Rental> page,
    @Param("query") RentalPageRequest query
  );

  List<RentalImageRow> selectImagesByRentalIds(
    @Param("rentalIds") List<Long> rentalIds
  );

  List<RentalStationRow> selectStationsByRentalIds(
    @Param("rentalIds") List<Long> rentalIds
  );

  List<RentalFeatureRow> selectFeaturesByRentalIds(
    @Param("rentalIds") List<Long> rentalIds
  );

  Rental selectPublicById(@Param("id") Long id);

  RentalAgentRow selectAgentByRentalId(
    @Param("rentalId") Long rentalId
  );

  RentalManagementCompanyRow selectManagementCompanyByRentalId(
    @Param("rentalId") Long rentalId
  );

  List<RentalFeatureOptionVO> selectFeatureOptions();

  IPage<Rental> searchMap(
    Page<Rental> page,
    @Param("query") RentalMapRequest request
  );

  List<Rental> selectPublicByIds(
    @Param("rentalIds") List<Long> rentalIds
  );


}
