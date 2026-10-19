package com.hikari.rent.user.repository.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.hikari.rent.user.mapper.BrowsingHistoryMapper;
import com.hikari.rent.user.repository.BrowsingHistoryRepository;
import com.hikari.rent.user.repository.model.BrowsingHistoryRow;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class BrowsingHistoryRepositoryImpl
  implements BrowsingHistoryRepository {

  private static final int MAX_HISTORY_SIZE = 30;

  @Resource
  private BrowsingHistoryMapper browsingHistoryMapper;

  @Override
  public List<BrowsingHistoryRow> findRecent(
    Long userId
  ) {

    validateUserId(userId);

    return browsingHistoryMapper.selectRecent(userId);
  }

  @Override
  @Transactional
  public void replaceSnapshot(
    Long userId,
    List<BrowsingHistoryRow> records
  ) {

    validateUserId(userId);

    if (records == null) {
      throw new IllegalArgumentException(
        "History snapshot must not be null"
      );
    }

    if (records.size() > MAX_HISTORY_SIZE) {
      throw new IllegalArgumentException(
        "History snapshot must contain at most 30 records"
      );
    }

    Set<Long> rentalIds = new HashSet<>();

    // Validate the entire snapshot before deleting existing records.
    for (BrowsingHistoryRow record : records) {

      if (
        record == null
          || record.getRentalId() == null
          || record.getRentalId() <= 0
          || record.getLastViewedAt() == null
      ) {
        throw new IllegalArgumentException(
          "History record contains invalid fields"
        );
      }

      if (!rentalIds.add(record.getRentalId())) {
        throw new IllegalArgumentException(
          "History snapshot contains duplicate rental IDs"
        );
      }
    }

    browsingHistoryMapper.deleteByUserId(userId);

    if (!records.isEmpty()) {
      browsingHistoryMapper.insertBatch(
        userId,
        records
      );
    }
  }

  private void validateUserId(Long userId) {

    if (userId == null || userId <= 0) {
      throw new IllegalArgumentException(
        "User ID must be positive"
      );
    }
  }
}
