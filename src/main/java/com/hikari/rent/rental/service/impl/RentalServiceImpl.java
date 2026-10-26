package com.hikari.rent.rental.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hikari.rent.common.result.PageResult;
import com.hikari.rent.file.service.FileUrlService;
import com.hikari.rent.rental.dto.RentalMapRequest;
import com.hikari.rent.rental.dto.RentalPageRequest;
import com.hikari.rent.rental.entity.Rental;
import com.hikari.rent.rental.exception.RentalNotFoundException;
import com.hikari.rent.rental.repository.RentalRepository;
import com.hikari.rent.rental.repository.model.*;
import com.hikari.rent.rental.search.model.RentalSearchCriteria;
import com.hikari.rent.rental.search.repository.RentalSearchRepository;
import com.hikari.rent.rental.service.RentalService;
import com.hikari.rent.rental.vo.*;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class RentalServiceImpl implements RentalService {

  @Resource
  private  RentalRepository rentalRepository;

  @Resource
  private FileUrlService fileUrlService;

  @Resource
  private RentalSearchRepository rentalSearchRepository;

  private static final int MAP_RESULT_LIMIT = 200;

  @Override
  public PageResult<RentalListVO> searchPage(
    RentalPageRequest request
  ) {
    RentalSearchCriteria criteria =
      new RentalSearchCriteria(
        request.getPage(),
        request.getPageSize(),
        request.getKeyword(),
        request.getAreaCode(),
        request.getMinRent(),
        request.getMaxRent(),
        request.getPropertyType(),
        request.getFloorPlans(),
        request.getMaxBuildingAge(),
        request.getStationId(),
        request.getMaxWalkingMinutes(),
        request.getFeatureCodes(),
        request.getSort()
      );

    PageResult<Long> searchResult =
      rentalSearchRepository.searchPage(criteria);

    List<Rental> rentals =
      loadOrderedPublicRentals(
        searchResult.records()
      );

    if (rentals.isEmpty()) {
      return new PageResult<>(
        searchResult.pageNum(),
        searchResult.pageSize(),
        searchResult.total(),
        List.of()
      );
    }

    List<RentalListVO> records =
      assembleListVOs(rentals);

    return new PageResult<>(
      searchResult.pageNum(),
      searchResult.pageSize(),
      searchResult.total(),
      records
    );
  }

  private List<RentalListVO> assembleListVOs(
    List<Rental> rentals
  ) {
    if (rentals.isEmpty()) {
      return List.of();
    }

    List<Long> rentalIds = rentals.stream()
      .map(Rental::getId)
      .toList();

    Map<Long, List<RentalImageRow>> imagesByRental =
      rentalRepository.findImagesByRentalIds(rentalIds)
        .stream()
        .collect(Collectors.groupingBy(
          RentalImageRow::getRentalId
        ));

    Map<Long, List<RentalStationRow>> stationsByRental =
      rentalRepository.findStationsByRentalIds(rentalIds)
        .stream()
        .collect(Collectors.groupingBy(
          RentalStationRow::getRentalId
        ));

    Map<Long, List<RentalFeatureRow>> featuresByRental =
      rentalRepository.findFeaturesByRentalIds(rentalIds)
        .stream()
        .collect(Collectors.groupingBy(
          RentalFeatureRow::getRentalId
        ));

    LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

    return rentals.stream()
      .map(rental -> toListVO(
        rental,
        imagesByRental.getOrDefault(
          rental.getId(),
          List.of()
        ),
        stationsByRental.getOrDefault(
          rental.getId(),
          List.of()
        ),
        featuresByRental.getOrDefault(
          rental.getId(),
          List.of()
        ),
        now
      ))
      .toList();
  }
  private RentalListVO toListVO(
    Rental rental,
    List<RentalImageRow> images,
    List<RentalStationRow> stations,
    List<RentalFeatureRow> features,
    LocalDateTime now
  ) {
    RentalListVO vo = new RentalListVO();

    vo.setId(rental.getId());
    vo.setAreaCode(rental.getAreaCode());
    vo.setName(rental.getName());
    vo.setAddress(rental.getAddress());
    vo.setPropertyType(
      rental.getPropertyType() == null
        ? null
        : rental.getPropertyType().getValue()
    );
    vo.setMonthlyRent(rental.getMonthlyRent());
    vo.setManagementFee(rental.getManagementFee());
    vo.setDepositMonths(rental.getDepositMonths());
    vo.setKeyMoneyMonths(rental.getKeyMoneyMonths());

    vo.setFloorPlan(rental.getFloorPlan());

    // Entity 是 areaSqm，前端字段是 area。
    vo.setArea(rental.getAreaSqm());

    vo.setFloor(rental.getFloor());
    vo.setTotalFloors(rental.getTotalFloors());
    vo.setBuiltYear(rental.getBuiltYear());
    vo.setBuiltMonth(rental.getBuiltMonth());

    vo.setLatitude(rental.getLatitude());
    vo.setLongitude(rental.getLongitude());

    vo.setIsNew(
      isNewRental(rental.getPublishedAt(), now)
    );

    fillImages(vo, images);
    fillStations(vo, stations);
    fillFeatures(vo, features);

    return vo;
  }

  private void fillImages(
    RentalListVO vo,
    List<RentalImageRow> images
  ) {
    // XML 已按 sort_order、id 排序。
    List<RentalImageRow> photos = images.stream()
      .filter(image -> "PHOTO".equals(image.getKind()))
      .toList();

    vo.setImageCount(photos.size());

    if (!photos.isEmpty()) {
      vo.setImage(
        fileUrlService.getPublicUrl(
          photos.getFirst().getStorageKey()
        )
      );
    }

    images.stream()
      .filter(image ->
        "FLOOR_PLAN".equals(image.getKind())
      )
      .findFirst()
      .ifPresent(image ->
        vo.setFloorPlanImage(
          fileUrlService.getPublicUrl(
            image.getStorageKey()
          )
        )
      );
  }

  private void fillStations(
    RentalListVO vo,
    List<RentalStationRow> stations
  ) {
    List<RentalStationRow> sortedStations =
      stations.stream()
        .sorted(
          Comparator
            .comparing(
              RentalStationRow::getWalkingMinutes
            )
            .thenComparing(
              RentalStationRow::getStationId
            )
        )
        .toList();

    List<RentalStationVO> stationVOs =
      sortedStations.stream()
        .map(row -> {
          RentalStationVO station =
            new RentalStationVO();

          station.setId(row.getStationId());
          station.setName(row.getName());
          station.setLine(row.getLineName());
          station.setWalkingMinutes(
            row.getWalkingMinutes()
          );

          return station;
        })
        .toList();

    vo.setStations(stationVOs);

    if (!stationVOs.isEmpty()) {
      RentalStationVO nearest =
        stationVOs.getFirst();

      vo.setStation(nearest.getName());
      vo.setWalkingMinutes(
        nearest.getWalkingMinutes()
      );
    }
  }

  private void fillFeatures(
    RentalListVO vo,
    List<RentalFeatureRow> features
  ) {
    vo.setAmenities(
      features.stream()
        .filter(feature ->
          "AMENITY".equals(
            feature.getCategory()
          )
        )
        .map(RentalFeatureRow::getName)
        .distinct()
        .toList()
    );

    vo.setConditions(
      features.stream()
        .filter(feature ->
          "CONDITION".equals(
            feature.getCategory()
          )
        )
        .map(RentalFeatureRow::getName)
        .distinct()
        .toList()
    );
  }

  private boolean isNewRental(
    LocalDateTime publishedAt,
    LocalDateTime now
  ) {
    return publishedAt != null
      && !publishedAt.isAfter(now)
      && !publishedAt.isBefore(now.minusDays(7));
  }

  @Override
  @Transactional(readOnly = true)
  public RentalDetailVO getDetail(Long id) {
    // 只查询允许公开访问的房源。
    Rental rental = rentalRepository.findPublicById(id)
      .orElseThrow(RentalNotFoundException::new);

    List<Long> rentalIds = List.of(rental.getId());

    List<RentalImageRow> images =
      rentalRepository.findImagesByRentalIds(rentalIds);

    List<RentalStationRow> stations =
      rentalRepository.findStationsByRentalIds(rentalIds);

    List<RentalFeatureRow> features =
      rentalRepository.findFeaturesByRentalIds(rentalIds);

    // 复用已有列表字段组装，避免重复设置相同属性。
    RentalListVO summary = toListVO(
      rental,
      images,
      stations,
      features,
      LocalDateTime.now(ZoneOffset.UTC)
    );

    RentalDetailVO detail = new RentalDetailVO();
    BeanUtils.copyProperties(summary, detail);

    // 轮播照片，户型图由父类的 floorPlanImage 提供。
    detail.setImages(
      images.stream()
        .filter(image ->
          "PHOTO".equals(image.getKind())
        )
        .map(image ->
          fileUrlService.getPublicUrl(
            image.getStorageKey()
          )
        )
        .filter(Objects::nonNull)
        .filter(url -> !url.isBlank())
        .toList()
    );

    // 费用
    detail.setBrokerageFee(rental.getBrokerageFee());
    detail.setGuaranteeFee(rental.getGuaranteeFee());
    detail.setRenewalFee(rental.getRenewalFee());

    // 建筑信息
    detail.setDirection(rental.getDirection());
    detail.setBuildingStructure(
      rental.getBuildingStructure()
    );

    // 合同、入住信息
    detail.setAvailableFrom(formatAvailableFrom(rental));
    detail.setContractType(rental.getContractType());
    detail.setContractPeriod(
      formatContractPeriod(rental.getContractMonths())
    );
    detail.setGuaranteeCompany(
      rental.getGuaranteeCompany()
    );

    detail.setDescription(rental.getDescription());

    // 公开房源应有可用担当者。
    RentalAgentRow agent =
      rentalRepository.findAgentByRentalId(id)
        .orElseThrow(RentalNotFoundException::new);

    detail.setAgent(toAgentVO(agent));

    // 管理会社允许未配置。
    detail.setManagementCompany(
      rentalRepository.findManagementCompanyByRentalId(id)
        .map(this::toManagementCompanyVO)
        .orElse(null)
    );

    return detail;
  }

  private RentalAgentVO toAgentVO(RentalAgentRow row) {
    RentalAgentVO vo = new RentalAgentVO();

    vo.setId(row.getId().toString());
    vo.setName(row.getName());
    vo.setAvatar(
      fileUrlService.getPublicUrl(
        row.getAvatarStorageKey()
      )
    );
    vo.setCompany(row.getCompany());
    vo.setBusinessHours(row.getBusinessHours());
    vo.setClosedDays(row.getClosedDays());

    // 电话由后续需要登录的专用接口返回。
    vo.setPhone(null);

    return vo;
  }

  private RentalManagementCompanyVO toManagementCompanyVO(
    RentalManagementCompanyRow row
  ) {
    RentalManagementCompanyVO vo =
      new RentalManagementCompanyVO();

    vo.setName(row.getName());
    vo.setAddress(row.getAddress());
    vo.setLicenseNumber(row.getLicenseNumber());
    vo.setEmail(row.getEmail());

    return vo;
  }

  private String formatAvailableFrom(Rental rental) {
    String text = rental.getAvailableFromText();

    if (text != null && !text.isBlank()) {
      return text.trim();
    }

    if (rental.getAvailableFrom() != null) {
      return rental.getAvailableFrom().toString();
    }

    return "要確認";
  }

  private String formatContractPeriod(Integer months) {
    if (months == null || months <= 0) {
      return "要確認";
    }

    int years = months / 12;
    int remainingMonths = months % 12;

    if (years == 0) {
      return remainingMonths + "か月";
    }

    if (remainingMonths == 0) {
      return years + "年";
    }

    return years + "年" + remainingMonths + "か月";
  }

  @Override
  @Transactional(readOnly = true)
  public List<RentalFeatureOptionVO> getFeatureOptions() {
    return rentalRepository.findFeatureOptions();
  }

  @Override
  @Transactional(readOnly = true)
  public RentalMapVO searchMap(RentalMapRequest request) {
    List<Rental> candidates = rentalRepository.searchMap(
      request,
      MAP_RESULT_LIMIT + 1
    );

    boolean hasMore = candidates.size() > MAP_RESULT_LIMIT;

    List<Rental> rentals = hasMore
      ? candidates.subList(0, MAP_RESULT_LIMIT)
      : candidates;

    return new RentalMapVO(
      assembleListVOs(rentals),
      hasMore,
      MAP_RESULT_LIMIT
    );
  }

  @Override
  @Transactional(readOnly = true)
  public List<RentalListVO> getPublicListByIds(
    List<Long> rentalIds
  ) {
    if (rentalIds == null || rentalIds.isEmpty()) {
      return List.of();
    }

    List<Rental> rentals =
      rentalRepository.findPublicByIds(rentalIds);

    Map<Long, Rental> rentalsById = rentals.stream()
      .collect(Collectors.toMap(
        Rental::getId,
        rental -> rental
      ));

    // Preserve the input order supplied by the favorites query.
    List<Rental> orderedRentals = rentalIds.stream()
      .distinct()
      .map(rentalsById::get)
      .filter(java.util.Objects::nonNull)
      .toList();

    return assembleListVOs(orderedRentals);
  }

  private List<Rental> loadOrderedPublicRentals(
    List<Long> rentalIds
  ) {
    if (rentalIds.isEmpty()) {
      return List.of();
    }

    Map<Long, Rental> rentalsById =
      rentalRepository
        .findPublicRentalsByIds(rentalIds)
        .stream()
        .collect(Collectors.toMap(
          Rental::getId,
          Function.identity()
        ));

    // Preserve Elasticsearch ordering and exclude unavailable rentals.
    return rentalIds.stream()
      .map(rentalsById::get)
      .filter(Objects::nonNull)
      .toList();
  }
}
