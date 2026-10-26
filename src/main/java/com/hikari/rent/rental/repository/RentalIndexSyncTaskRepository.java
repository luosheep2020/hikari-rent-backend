package com.hikari.rent.rental.repository;

import com.hikari.rent.rental.mapper.RentalIndexSyncTaskMapper;
import com.hikari.rent.rental.repository.model.RentalIndexSyncTaskRow;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public class RentalIndexSyncTaskRepository {

  @Resource
  private RentalIndexSyncTaskMapper rentalIndexSyncTaskMapper;

  public void enqueue(Long rentalId) {
    rentalIndexSyncTaskMapper.enqueue(rentalId);
  }

  public RentalIndexSyncTaskRow findNextForUpdate() {
    return rentalIndexSyncTaskMapper
      .selectNextForUpdate();
  }

  public void deleteTask(Long rentalId) {
    rentalIndexSyncTaskMapper.deleteTask(rentalId);
  }

  public void reschedule(
    Long rentalId,
    int retryCount,
    LocalDateTime nextAttemptAt,
    String lastError
  ) {
    rentalIndexSyncTaskMapper.reschedule(
      rentalId,
      retryCount,
      nextAttemptAt,
      lastError
    );
  }
}
