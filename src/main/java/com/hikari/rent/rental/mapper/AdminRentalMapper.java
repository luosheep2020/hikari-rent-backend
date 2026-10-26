package com.hikari.rent.rental.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hikari.rent.rental.dto.admin.AdminRentalCreateRequest;
import com.hikari.rent.rental.dto.admin.AdminRentalPageRequest;
import com.hikari.rent.rental.model.RentalAccessScope;
import com.hikari.rent.rental.repository.model.AdminRentalInsertRow;
import com.hikari.rent.rental.repository.model.AdminRentalReviewRow;
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
    @Param("scope") RentalAccessScope scope
  );

  AdminRentalDetailVO selectDetail(
    @Param("rentalId") Long rentalId,
    @Param("scope") RentalAccessScope scope
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

  int updateDraft(
    @Param("rental") AdminRentalUpdateRow rental,
    @Param("scope") RentalAccessScope scope
  );

  int insertStationRelations(
    @Param("rentalId") Long rentalId,
    @Param("stations")
    List<AdminRentalCreateRequest.Station> stations
  );

  int deleteStationRelations(
    @Param("rentalId") Long rentalId
  );

  int insertFeatureRelations(
    @Param("rentalId") Long rentalId,
    @Param("featureCodes") List<String> featureCodes
  );

  int deleteFeatureRelations(
    @Param("rentalId") Long rentalId
  );

  List<Long> selectUsableImageFileIdsForUpdate(
    @Param("rentalId") Long rentalId,
    @Param("userId") Long userId,
    @Param("fileIds") List<Long> fileIds
  );

  int insertImageRelations(
    @Param("rentalId") Long rentalId,
    @Param("images")
    List<AdminRentalCreateRequest.Image> images
  );

  int deleteImageRelations(
    @Param("rentalId") Long rentalId
  );

  AdminRentalReviewRow selectReviewTargetForUpdate(
    @Param("rentalId") Long rentalId,
    @Param("scope") RentalAccessScope scope
  );

  int submitReview(
    @Param("rentalId") Long rentalId,
    @Param("scope") RentalAccessScope scope,
    @Param("version") long version
  );


  int review(
    @Param("rentalId") Long rentalId,
    @Param("reviewerId") Long reviewerId,
    @Param("version") long version,
    @Param("reviewStatus") int reviewStatus,
    @Param("remark") String remark
  );

  int countPublishableRental(
    @Param("rentalId") Long rentalId
  );

  int publish(
    @Param("rentalId") Long rentalId,
    @Param("scope") RentalAccessScope scope,
    @Param("version") long version
  );

  int unpublish(
    @Param("rentalId") Long rentalId,
    @Param("scope") RentalAccessScope scope,
    @Param("version") long version
  );

  int markRented(
    @Param("rentalId") Long rentalId,
    @Param("scope") RentalAccessScope scope,
    @Param("version") long version
  );
}
