package com.hikari.rent.user.repository;

import java.util.List;

import com.hikari.rent.user.repository.model.BrowsingHistoryRow;

public interface BrowsingHistoryRepository {

  List<BrowsingHistoryRow> findRecent(Long userId);

  void replaceSnapshot(
    Long userId,
    List<BrowsingHistoryRow> records
  );
}
