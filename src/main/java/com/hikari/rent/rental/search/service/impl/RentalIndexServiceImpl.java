package com.hikari.rent.rental.search.service.impl;

import com.hikari.rent.rental.repository.RentalRepository;
import com.hikari.rent.rental.repository.model.RentalFeatureRow;
import com.hikari.rent.rental.repository.model.RentalIndexAreaRow;
import com.hikari.rent.rental.repository.model.RentalIndexRow;
import com.hikari.rent.rental.repository.model.RentalStationRow;
import com.hikari.rent.rental.search.document.RentalSearchDocument;
import com.hikari.rent.rental.search.model.RentalIndexImportResult;
import com.hikari.rent.rental.search.repository.RentalSearchRepository;
import com.hikari.rent.rental.search.service.RentalIndexService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RentalIndexServiceImpl
  implements RentalIndexService {

  private static final int BATCH_SIZE = 500;

  @Resource
  private RentalRepository rentalRepository;

  @Resource
  private RentalSearchRepository rentalSearchRepository;

  @Override
  public RentalIndexImportResult importPublicRentals() {
    long upperId =
      rentalRepository.findIndexUpperId();

    Map<String, RentalIndexAreaRow> areas =
      loadAreas();

    long afterId = 0L;
    long processedCount = 0L;

    log.info(
      "Starting rental index import, upperId={}",
      upperId
    );

    while (afterId < upperId) {
      List<RentalIndexRow> rentals =
        rentalRepository.findPublicIndexBatch(
          afterId,
          upperId,
          BATCH_SIZE
        );

      if (rentals.isEmpty()) {
        break;
      }

      List<Long> rentalIds = rentals.stream()
        .map(RentalIndexRow::getId)
        .toList();

      Map<Long, List<RentalStationRow>> stations =
        rentalRepository
          .findStationsByRentalIds(rentalIds)
          .stream()
          .collect(Collectors.groupingBy(
            RentalStationRow::getRentalId
          ));

      Map<Long, List<RentalFeatureRow>> features =
        rentalRepository
          .findFeaturesByRentalIds(rentalIds)
          .stream()
          .collect(Collectors.groupingBy(
            RentalFeatureRow::getRentalId
          ));

      List<RentalSearchDocument> documents =
        rentals.stream()
          .map(rental -> toDocument(
            rental,
            areas,
            stations.getOrDefault(
              rental.getId(),
              List.of()
            ),
            features.getOrDefault(
              rental.getId(),
              List.of()
            )
          ))
          .toList();

      rentalSearchRepository.saveBatch(documents);

      afterId = rentals.getLast().getId();
      processedCount += documents.size();

      log.info(
        "Imported rental batch, count={}, total={}, lastId={}",
        documents.size(),
        processedCount,
        afterId
      );
    }

    rentalSearchRepository.refresh();

    log.info(
      "Rental index import completed, count={}, lastId={}",
      processedCount,
      afterId
    );

    return new RentalIndexImportResult(
      processedCount,
      afterId,
      upperId
    );
  }

  private Map<String, RentalIndexAreaRow> loadAreas() {
    Map<String, RentalIndexAreaRow> result =
      new HashMap<>();

    for (
      RentalIndexAreaRow area :
      rentalRepository.findIndexAreas()
    ) {
      result.put(area.getCode(), area);
    }

    return result;
  }

  private RentalSearchDocument toDocument(
    RentalIndexRow rental,
    Map<String, RentalIndexAreaRow> areas,
    List<RentalStationRow> stations,
    List<RentalFeatureRow> features
  ) {
    RentalSearchDocument document =
      new RentalSearchDocument();

    document.setId(rental.getId().toString());
    document.setRentalId(rental.getId());

    document.setName(rental.getName());
    document.setAddress(rental.getAddress());
    document.setDescription(rental.getDescription());

    document.setAreaCode(rental.getAreaCode());

    document.setAreaPathCodes(
      buildAreaPath(rental.getAreaCode(), areas)
    );

    document.setPropertyType(
      rental.getPropertyType()
    );

    document.setMonthlyRent(
      rental.getMonthlyRent()
    );

    document.setManagementFee(
      rental.getManagementFee()
    );

    document.setFloorPlan(rental.getFloorPlan());
    document.setArea(rental.getArea());

    document.setBuiltDate(
      buildBuiltDate(rental)
    );

    document.setFeatureCodes(
      features.stream()
        .map(RentalFeatureRow::getCode)
        .filter(code ->
          code != null && !code.isBlank()
        )
        .distinct()
        .toList()
    );

    document.setStations(
      stations.stream()
        .map(this::toStation)
        .toList()
    );

    if (
      rental.getLatitude() != null
        && rental.getLongitude() != null
    ) {
      RentalSearchDocument.Location location =
        new RentalSearchDocument.Location();

      location.setLat(
        rental.getLatitude().doubleValue()
      );

      location.setLon(
        rental.getLongitude().doubleValue()
      );

      document.setLocation(location);
    }

    if (rental.getPublishedAt() != null) {
      // Database timestamps follow the project's UTC convention.
      document.setPublishedAt(
        rental.getPublishedAt()
          .toInstant(ZoneOffset.UTC)
          .toString()
      );
    }

    document.setCompanyId(
      rental.getCompanyId().toString()
    );

    document.setAgentId(
      rental.getAgentId().toString()
    );

    return document;
  }

  private RentalSearchDocument.Station toStation(
    RentalStationRow row
  ) {
    RentalSearchDocument.Station station =
      new RentalSearchDocument.Station();

    station.setId(
      row.getStationId().toString()
    );

    station.setName(row.getName());

    station.setWalkingMinutes(
      row.getWalkingMinutes()
    );

    return station;
  }

  private String buildBuiltDate(
    RentalIndexRow rental
  ) {
    if (
      rental.getBuiltYear() == null
        || rental.getBuiltMonth() == null
    ) {
      return null;
    }

    try {
      return LocalDate.of(
        rental.getBuiltYear(),
        rental.getBuiltMonth(),
        1
      ).toString();

    } catch (DateTimeException ex) {
      throw new IllegalStateException(
        "Invalid building date for rental "
          + rental.getId(),
        ex
      );
    }
  }

  private List<String> buildAreaPath(
    String areaCode,
    Map<String, RentalIndexAreaRow> areas
  ) {
    if (areaCode == null || areaCode.isBlank()) {
      throw new IllegalStateException(
        "Rental area code is missing"
      );
    }

    List<String> path = new ArrayList<>();
    Set<String> visited = new HashSet<>();

    String currentCode = areaCode;

    while (currentCode != null) {
      if (!visited.add(currentCode)) {
        throw new IllegalStateException(
          "Circular area hierarchy: "
            + currentCode
        );
      }

      RentalIndexAreaRow area =
        areas.get(currentCode);

      if (area == null) {
        throw new IllegalStateException(
          "Area not found: "
            + currentCode
        );
      }

      path.add(area.getCode());
      currentCode = area.getParentCode();
    }

    Collections.reverse(path);

    return List.copyOf(path);
  }

  @Override
  @Transactional(
    propagation = Propagation.NOT_SUPPORTED
  )
  public void syncRental(Long rentalId) {
    if (rentalId == null || rentalId <= 0) {
      throw new IllegalArgumentException(
        "rentalId must be greater than zero"
      );
    }

    // Reuse the public visibility query for this exact rental ID.
    List<RentalIndexRow> rows =
      rentalRepository.findPublicIndexBatch(
        rentalId - 1,
        rentalId,
        1
      );

    if (rows.isEmpty()) {
      rentalSearchRepository.deleteById(rentalId);

      log.info(
        "Removed rental document, rentalId={}",
        rentalId
      );

      return;
    }

    RentalIndexRow rental = rows.getFirst();

    List<Long> rentalIds = List.of(rentalId);

    List<RentalStationRow> stations =
      rentalRepository.findStationsByRentalIds(
        rentalIds
      );

    List<RentalFeatureRow> features =
      rentalRepository.findFeaturesByRentalIds(
        rentalIds
      );

    Map<String, RentalIndexAreaRow> areas =
      loadAreas();

    RentalSearchDocument document =
      toDocument(
        rental,
        areas,
        stations,
        features
      );

    rentalSearchRepository.saveBatch(
      List.of(document)
    );

    log.info(
      "Updated rental document, rentalId={}",
      rentalId
    );
  }
}
