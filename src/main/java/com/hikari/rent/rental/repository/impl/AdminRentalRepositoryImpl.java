package com.hikari.rent.rental.repository.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hikari.rent.rental.dto.admin.AdminRentalCreateRequest;
import com.hikari.rent.rental.dto.admin.AdminRentalPageRequest;
import com.hikari.rent.rental.mapper.AdminRentalMapper;
import com.hikari.rent.rental.model.RentalAccessScope;
import com.hikari.rent.rental.repository.AdminRentalRepository;
import com.hikari.rent.rental.repository.model.AdminRentalInsertRow;
import com.hikari.rent.rental.repository.model.AdminRentalReviewRow;
import com.hikari.rent.rental.repository.model.AdminRentalUpdateRow;
import com.hikari.rent.rental.vo.admin.AdminRentalDetailVO;
import com.hikari.rent.rental.vo.admin.AdminRentalListVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AdminRentalRepositoryImpl
  implements AdminRentalRepository {

  @Resource
  private AdminRentalMapper adminRentalMapper;

  @Override
  public Long findActiveCompanyId(Long userId) {
    return adminRentalMapper.selectActiveCompanyId(
      userId
    );
  }

  @Override
  public IPage<AdminRentalListVO> searchPage(
    AdminRentalPageRequest request,
    RentalAccessScope scope
  ) {
    Page<AdminRentalListVO> page = new Page<>(
      request.getPage(),
      request.getPageSize()
    );

    // Preserve parameter mappings in the generated count query.
    page.setOptimizeCountSql(false);

    return adminRentalMapper.searchPage(
      page,
      request,
      scope
    );
  }

  @Override
  public AdminRentalDetailVO findDetail(
    Long rentalId,
    RentalAccessScope scope
  ) {
    return adminRentalMapper.selectDetail(
      rentalId,
      scope
    );
  }

  @Override
  public boolean municipalityExists(String areaCode) {
    return adminRentalMapper.countMunicipality(
      areaCode
    ) == 1;
  }

  @Override
  public boolean activeEmployeeExists(
    Long userId,
    Long companyId
  ) {
    return adminRentalMapper.countActiveEmployee(
      userId,
      companyId
    ) == 1;
  }

  @Override
  public boolean activeCompanyExists(Long companyId) {
    return adminRentalMapper.countActiveCompany(
      companyId
    ) == 1;
  }

  @Override
  public int countActiveStations(
    List<Long> stationIds
  ) {
    if (stationIds == null || stationIds.isEmpty()) {
      return 0;
    }

    return adminRentalMapper.countActiveStations(
      stationIds
    );
  }

  @Override
  public int countActiveFeatures(
    List<String> featureCodes
  ) {
    if (
      featureCodes == null
        || featureCodes.isEmpty()
    ) {
      return 0;
    }

    return adminRentalMapper.countActiveFeatures(
      featureCodes
    );
  }

  @Override
  public void insertDraft(
    AdminRentalInsertRow rental
  ) {
    int affected =
      adminRentalMapper.insertDraft(rental);

    if (affected != 1 || rental.getId() == null) {
      throw new IllegalStateException(
        "Rental draft insert did not return a generated ID"
      );
    }
  }

  @Override
  public boolean updateDraft(
    AdminRentalUpdateRow rental,
    RentalAccessScope scope
  ) {
    return adminRentalMapper.updateDraft(
      rental,
      scope
    ) == 1;
  }

  @Override
  public int insertStationRelations(
    Long rentalId,
    List<AdminRentalCreateRequest.Station> stations
  ) {
    if (stations == null || stations.isEmpty()) {
      return 0;
    }

    return adminRentalMapper.insertStationRelations(
      rentalId,
      stations
    );
  }

  @Override
  public void deleteStationRelations(
    Long rentalId
  ) {
    adminRentalMapper.deleteStationRelations(
      rentalId
    );
  }

  @Override
  public int insertFeatureRelations(
    Long rentalId,
    List<String> featureCodes
  ) {
    if (
      featureCodes == null
        || featureCodes.isEmpty()
    ) {
      return 0;
    }

    return adminRentalMapper.insertFeatureRelations(
      rentalId,
      featureCodes
    );
  }

  @Override
  public void deleteFeatureRelations(
    Long rentalId
  ) {
    adminRentalMapper.deleteFeatureRelations(
      rentalId
    );
  }

  @Override
  public List<Long> lockUsableImageFileIds(
    Long rentalId,
    Long userId,
    List<Long> fileIds
  ) {
    if (fileIds == null || fileIds.isEmpty()) {
      return List.of();
    }

    return adminRentalMapper
      .selectUsableImageFileIdsForUpdate(
        rentalId,
        userId,
        fileIds
      );
  }

  @Override
  public int insertImageRelations(
    Long rentalId,
    List<AdminRentalCreateRequest.Image> images
  ) {
    if (images == null || images.isEmpty()) {
      return 0;
    }

    return adminRentalMapper.insertImageRelations(
      rentalId,
      images
    );
  }

  @Override
  public void deleteImageRelations(
    Long rentalId
  ) {
    adminRentalMapper.deleteImageRelations(
      rentalId
    );
  }

  @Override
  public AdminRentalReviewRow findReviewTargetForUpdate(
    Long rentalId,
    RentalAccessScope scope
  ) {
    return adminRentalMapper
      .selectReviewTargetForUpdate(
        rentalId,
        scope
      );
  }

  @Override
  public boolean submitReview(
    Long rentalId,
    RentalAccessScope scope,
    long version
  ) {
    return adminRentalMapper.submitReview(
      rentalId,
      scope,
      version
    ) == 1;
  }

  @Override
  public boolean review(
    Long rentalId,
    Long reviewerId,
    long version,
    int reviewStatus,
    String remark
  ) {
    return adminRentalMapper.review(
      rentalId,
      reviewerId,
      version,
      reviewStatus,
      remark
    ) == 1;
  }

  @Override
  public boolean meetsPublicationRequirements(Long rentalId) {
    return adminRentalMapper.countPublishableRental(rentalId) == 1;
  }

  @Override
  public boolean publish(
    Long rentalId,
    RentalAccessScope scope,
    long version
  ) {
    return adminRentalMapper.publish(
      rentalId,
      scope,
      version
    ) == 1;
  }

  @Override
  public boolean unpublish(
    Long rentalId,
    RentalAccessScope scope,
    long version
  ) {
    return adminRentalMapper.unpublish(
      rentalId,
      scope,
      version
    ) == 1;
  }

  @Override
  public boolean markRented(
    Long rentalId,
    RentalAccessScope scope,
    long version
  ) {
    return adminRentalMapper.markRented(
      rentalId,
      scope,
      version
    ) == 1;
  }
}
