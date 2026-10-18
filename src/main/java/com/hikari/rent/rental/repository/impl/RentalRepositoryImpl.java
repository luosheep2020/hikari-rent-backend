package com.hikari.rent.rental.repository.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.repository.AbstractRepository;
import com.baomidou.mybatisplus.spring.repository.CrudRepository;
import com.hikari.rent.rental.dto.RentalMapRequest;
import com.hikari.rent.rental.dto.RentalPageRequest;
import com.hikari.rent.rental.entity.Rental;
import com.hikari.rent.rental.mapper.RentalMapper;
import com.hikari.rent.rental.repository.RentalRepository;
import com.hikari.rent.rental.repository.model.*;
import com.hikari.rent.rental.vo.RentalFeatureOptionVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class RentalRepositoryImpl
  extends CrudRepository<RentalMapper, Rental>
  implements RentalRepository {

  @Resource
  private RentalMapper rentalMapper;

  @Override
  public IPage<Rental> searchPage(
    RentalPageRequest request
  ) {
    Page<Rental> page = new Page<>(
      request.getPage(),
      request.getPageSize()
    );
    page.setOptimizeCountSql(false);
    return getBaseMapper().searchPage(page, request);
  }

  @Override
  public List<RentalImageRow> findImagesByRentalIds(
    List<Long> rentalIds
  ) {
    if (rentalIds == null || rentalIds.isEmpty()) {
      return List.of();
    }

    return baseMapper.selectImagesByRentalIds(rentalIds);
  }

  @Override
  public List<RentalStationRow> findStationsByRentalIds(
    List<Long> rentalIds
  ) {
    if (rentalIds == null || rentalIds.isEmpty()) {
      return List.of();
    }

    return baseMapper.selectStationsByRentalIds(rentalIds);
  }

  @Override
  public List<RentalFeatureRow> findFeaturesByRentalIds(
    List<Long> rentalIds
  ) {
    if (rentalIds == null || rentalIds.isEmpty()) {
      return List.of();
    }

    return baseMapper.selectFeaturesByRentalIds(rentalIds);
  }

  @Override
  public Optional<Rental> findPublicById(Long id) {
    return Optional.ofNullable(
      baseMapper.selectPublicById(id)
    );
  }

  @Override
  public Optional<RentalAgentRow> findAgentByRentalId(Long rentalId) {
    return Optional.ofNullable(
      baseMapper.selectAgentByRentalId(rentalId));
  }

  @Override
  public Optional<RentalManagementCompanyRow> findManagementCompanyByRentalId(Long rentalId) {
    return Optional.ofNullable(
      baseMapper.selectManagementCompanyByRentalId(rentalId)
    );
  }

  @Override
  public List<RentalFeatureOptionVO> findFeatureOptions() {
    return baseMapper.selectFeatureOptions();
  }

  @Override
  public List<Rental> searchMap(
    RentalMapRequest request,
    int fetchSize
  ) {
    Page<Rental> page = new Page<>(
      1,
      fetchSize,
      false
    );

    return baseMapper.searchMap(page, request).getRecords();
  }

  @Override
  public List<Rental> findPublicByIds(
    List<Long> rentalIds
  ) {
    if (rentalIds == null || rentalIds.isEmpty()) {
      return List.of();
    }

    return baseMapper.selectPublicByIds(rentalIds);
  }
}
