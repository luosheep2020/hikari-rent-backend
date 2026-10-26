package com.hikari.rent.rental.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hikari.rent.rental.dto.admin.AdminRentalCreateRequest;
import com.hikari.rent.rental.dto.admin.AdminRentalPageRequest;
import com.hikari.rent.rental.model.RentalAccessScope;
import com.hikari.rent.rental.repository.model.AdminRentalInsertRow;
import com.hikari.rent.rental.repository.model.AdminRentalReviewRow;
import com.hikari.rent.rental.repository.model.AdminRentalUpdateRow;
import com.hikari.rent.rental.vo.admin.AdminRentalDetailVO;
import com.hikari.rent.rental.vo.admin.AdminRentalListVO;

import java.util.List;

public interface AdminRentalRepository {

  Long findActiveCompanyId(Long userId);

  IPage<AdminRentalListVO> searchPage(
    AdminRentalPageRequest request,
    RentalAccessScope scope
  );

  AdminRentalDetailVO findDetail(
    Long rentalId,
    RentalAccessScope scope
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

  boolean updateDraft(
    AdminRentalUpdateRow rental,
    RentalAccessScope scope
  );

  int insertStationRelations(
    Long rentalId,
    List<AdminRentalCreateRequest.Station> stations
  );

  void deleteStationRelations(Long rentalId);

  int insertFeatureRelations(
    Long rentalId,
    List<String> featureCodes
  );

  void deleteFeatureRelations(Long rentalId);

  List<Long> lockUsableImageFileIds(
    Long rentalId,
    Long userId,
    List<Long> fileIds
  );

  int insertImageRelations(
    Long rentalId,
    List<AdminRentalCreateRequest.Image> images
  );

  void deleteImageRelations(Long rentalId);

  AdminRentalReviewRow findReviewTargetForUpdate(
    Long rentalId,
    RentalAccessScope scope
  );

  boolean submitReview(
    Long rentalId,
    RentalAccessScope scope,
    long version
  );

  boolean review(
    Long rentalId,
    Long reviewerId,
    long version,
    int reviewStatus,
    String remark
  );

  boolean meetsPublicationRequirements(Long rentalId);

  boolean publish(
    Long rentalId,
    RentalAccessScope scope,
    long version
  );

  boolean unpublish(
    Long rentalId,
    RentalAccessScope scope,
    long version
  );

  boolean markRented(
    Long rentalId,
    RentalAccessScope scope,
    long version
  );
}
