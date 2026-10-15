package com.hikari.rent.rental.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.repository.IRepository;
import com.hikari.rent.rental.dto.RentalPageRequest;
import com.hikari.rent.rental.entity.Rental;
import com.hikari.rent.rental.repository.model.*;

import java.util.List;
import java.util.Optional;

public interface RentalRepository extends IRepository<Rental> {

  IPage<Rental> searchPage(RentalPageRequest request);

  List<RentalImageRow> findImagesByRentalIds(
    List<Long> rentalIds
  );

  List<RentalStationRow> findStationsByRentalIds(
    List<Long> rentalIds
  );

  List<RentalFeatureRow> findFeaturesByRentalIds(
    List<Long> rentalIds
  );

  Optional<Rental> findPublicById(Long id);

  Optional<RentalAgentRow> findAgentByRentalId(Long rentalId);

  Optional<RentalManagementCompanyRow> findManagementCompanyByRentalId(
    Long rentalId
  );
}
