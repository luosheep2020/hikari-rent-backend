package com.hikari.rent.rental.mapper;

import com.hikari.rent.rental.repository.model.RentalIndexSyncTaskRow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

@Mapper
public interface RentalIndexSyncTaskMapper {

  int enqueue(
    @Param("rentalId") Long rentalId
  );

  RentalIndexSyncTaskRow selectNextForUpdate();

  int deleteTask(
    @Param("rentalId") Long rentalId
  );

  int reschedule(
    @Param("rentalId") Long rentalId,
    @Param("retryCount") int retryCount,
    @Param("nextAttemptAt") LocalDateTime nextAttemptAt,
    @Param("lastError") String lastError
  );
}
