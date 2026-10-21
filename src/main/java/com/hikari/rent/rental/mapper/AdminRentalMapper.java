package com.hikari.rent.rental.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hikari.rent.rental.dto.admin.AdminRentalCreateRequest;
import com.hikari.rent.rental.dto.admin.AdminRentalPageRequest;
import com.hikari.rent.rental.repository.model.AdminRentalInsertRow;
import com.hikari.rent.rental.repository.model.AdminRentalUpdateRow;
import com.hikari.rent.rental.vo.admin.AdminRentalDetailVO;
import com.hikari.rent.rental.vo.admin.AdminRentalListVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminRentalMapper {

  Long selectActiveCompanyId(
    @Param("userId") Long userId
  );

  IPage<AdminRentalListVO> searchPage(
    IPage<AdminRentalListVO> page,
    @Param("query") AdminRentalPageRequest query,
    @Param("userId") Long userId,
    @Param("companyId") Long companyId
  );


  AdminRentalDetailVO selectDetail(
    @Param("rentalId") Long rentalId,
    @Param("userId") Long userId,
    @Param("companyId") Long companyId
  );

  int countMunicipality(
    @Param("areaCode") String areaCode
  );

  int countActiveEmployee(
    @Param("userId") Long userId,
    @Param("companyId") Long companyId
  );

  int countActiveCompany(
    @Param("companyId") Long companyId
  );

  int countActiveStations(
    @Param("stationIds") List<Long> stationIds
  );

  int countActiveFeatures(
    @Param("featureCodes") List<String> featureCodes
  );

  int insertDraft(
    @Param("rental") AdminRentalInsertRow rental
  );

  int insertStationRelations(
    @Param("rentalId") Long rentalId,
    @Param("stations")
    List<AdminRentalCreateRequest.Station> stations
  );

  int insertFeatureRelations(
    @Param("rentalId") Long rentalId,
    @Param("featureCodes") List<String> featureCodes
  );

  int updateDraft(
    @Param("rental") AdminRentalUpdateRow rental
  );

  int deleteStationRelations(
    @Param("rentalId") Long rentalId
  );

  int deleteFeatureRelations(
    @Param("rentalId") Long rentalId
  );
}
