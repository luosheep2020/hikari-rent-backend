package com.hikari.rent.rental.repository.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hikari.rent.rental.dto.admin.AdminRentalCreateRequest;
import com.hikari.rent.rental.dto.admin.AdminRentalPageRequest;
import com.hikari.rent.rental.mapper.AdminRentalMapper;
import com.hikari.rent.rental.repository.AdminRentalRepository;
import com.hikari.rent.rental.repository.model.AdminRentalInsertRow;
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
    Long userId,
    Long companyId
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
      userId,
      companyId
    );
  }

  @Override
  public AdminRentalDetailVO findDetail(
    Long rentalId,
    Long userId,
    Long companyId
  ) {
    return adminRentalMapper.selectDetail(
      rentalId,
      userId,
      companyId
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
  public int countActiveStations(List<Long> stationIds) {
    if (stationIds.isEmpty()) {
      return 0;
    }

    return adminRentalMapper.countActiveStations(
      stationIds
    );
  }

  @Override
  public int countActiveFeatures(List<String> featureCodes) {
    if (featureCodes.isEmpty()) {
      return 0;
    }

    return adminRentalMapper.countActiveFeatures(
      featureCodes
    );
  }

  @Override
  public void insertDraft(AdminRentalInsertRow rental) {
    int affected = adminRentalMapper.insertDraft(rental);

    if (affected != 1 || rental.getId() == null) {
      throw new IllegalStateException(
        "Rental draft insert did not return a generated ID"
      );
    }
  }

  @Override
  public int insertStationRelations(
    Long rentalId,
    List<AdminRentalCreateRequest.Station> stations
  ) {
    if (stations.isEmpty()) {
      return 0;
    }

    return adminRentalMapper.insertStationRelations(
      rentalId,
      stations
    );
  }

  @Override
  public int insertFeatureRelations(
    Long rentalId,
    List<String> featureCodes
  ) {
    if (featureCodes.isEmpty()) {
      return 0;
    }

    return adminRentalMapper.insertFeatureRelations(
      rentalId,
      featureCodes
    );
  }

  @Override
  public boolean updateDraft(
    AdminRentalUpdateRow rental
  ) {
    return adminRentalMapper.updateDraft(rental) == 1;
  }

  @Override
  public void deleteStationRelations(Long rentalId) {
    adminRentalMapper.deleteStationRelations(
      rentalId
    );
  }

  @Override
  public void deleteFeatureRelations(Long rentalId) {
    adminRentalMapper.deleteFeatureRelations(
      rentalId
    );
  }
}
