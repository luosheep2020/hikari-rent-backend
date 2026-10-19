package com.hikari.rent.user.mapper;

import java.util.List;

import com.hikari.rent.user.repository.model.BrowsingHistoryRow;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BrowsingHistoryMapper {

  List<BrowsingHistoryRow> selectRecent(
    @Param("userId") Long userId
  );

  int deleteByUserId(
    @Param("userId") Long userId
  );

  int insertBatch(
    @Param("userId") Long userId,
    @Param("records") List<BrowsingHistoryRow> records
  );
}
