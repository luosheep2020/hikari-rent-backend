package com.hikari.rent.rental.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hikari.rent.rental.dto.admin.AdminRentalCreateRequest;
import com.hikari.rent.rental.dto.admin.AdminRentalPageRequest;
import com.hikari.rent.rental.repository.model.AdminRentalInsertRow;
import com.hikari.rent.rental.repository.model.AdminRentalUpdateRow;
import com.hikari.rent.rental.vo.admin.AdminRentalDetailVO;
import com.hikari.rent.rental.vo.admin.AdminRentalListVO;

import java.util.List;

public interface AdminRentalRepository {

  Long findActiveCompanyId(Long userId);

  IPage<AdminRentalListVO> searchPage(
    AdminRentalPageRequest request,
    Long userId,
    Long companyId
  );

  AdminRentalDetailVO findDetail(
    Long rentalId,
    Long userId,
    Long companyId
  );


  boolean municipalityExists(String areaCode);

  boolean activeEmployeeExists(
    Long userId,
    Long companyId
  );

  boolean activeCompanyExists(Long companyId);

  int countActiveStations(List<Long> stationIds);

  int countActiveFeatures(List<String> featureCodes);

  void insertDraft(AdminRentalInsertRow rental);

  int insertStationRelations(
    Long rentalId,
    List<AdminRentalCreateRequest.Station> stations
  );

  int insertFeatureRelations(
    Long rentalId,
    List<String> featureCodes
  );

  boolean updateDraft(AdminRentalUpdateRow rental);

  void deleteStationRelations(Long rentalId);

  void deleteFeatureRelations(Long rentalId);
}
