package com.hikari.rent.rental.search.repository;

import com.hikari.rent.common.result.PageResult;
import com.hikari.rent.rental.search.document.RentalSearchDocument;
import com.hikari.rent.rental.search.model.RentalSearchCriteria;

import java.util.List;

public interface RentalSearchRepository {

  PageResult<Long> searchPage(
    RentalSearchCriteria criteria
  );

  void saveBatch(
    List<RentalSearchDocument> documents
  );

  void refresh();

  void deleteById(Long rentalId);
}
