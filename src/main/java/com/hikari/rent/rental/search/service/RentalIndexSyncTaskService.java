package com.hikari.rent.rental.search.service;

import com.hikari.rent.rental.repository.RentalIndexSyncTaskRepository;
import com.hikari.rent.rental.repository.model.RentalIndexSyncTaskRow;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Slf4j
@Service
public class RentalIndexSyncTaskService {

  @Resource
  private RentalIndexSyncTaskRepository taskRepository;

  @Resource
  private RentalIndexService rentalIndexService;

  @Transactional(
    propagation = Propagation.MANDATORY
  )
  public void requestSync(Long rentalId) {
    if (rentalId == null || rentalId <= 0) {
      throw new IllegalArgumentException(
        "rentalId must be greater than zero"
      );
    }

    taskRepository.enqueue(rentalId);
  }

  @Transactional(
    propagation = Propagation.REQUIRES_NEW
  )
  public boolean processNext() {
    RentalIndexSyncTaskRow task =
      taskRepository.findNextForUpdate();

    if (task == null) {
      return false;
    }

    try {
      rentalIndexService.syncRental(
        task.getRentalId()
      );

    } catch (RuntimeException ex) {
      reschedule(task, ex);
      return true;
    }

    taskRepository.deleteTask(
      task.getRentalId()
    );

    log.info(
      "Rental index sync task completed, rentalId={}",
      task.getRentalId()
    );

    return true;
  }

  private void reschedule(
    RentalIndexSyncTaskRow task,
    RuntimeException ex
  ) {
    int retryCount =
      Math.min(task.getRetryCount() + 1, 30);

    long delaySeconds = Math.min(
      300L,
      5L * (1L << Math.min(retryCount - 1, 6))
    );

    LocalDateTime nextAttemptAt =
      LocalDateTime.now(ZoneOffset.UTC)
        .plusSeconds(delaySeconds);

    String error = ex.getMessage();

    if (error == null || error.isBlank()) {
      error = ex.getClass().getSimpleName();
    }

    if (error.length() > 1000) {
      error = error.substring(0, 1000);
    }

    taskRepository.reschedule(
      task.getRentalId(),
      retryCount,
      nextAttemptAt,
      error
    );

    log.warn(
      "Rental index sync failed, rentalId={}, retryCount={}, nextAttemptAt={}",
      task.getRentalId(),
      retryCount,
      nextAttemptAt,
      ex
    );
  }
}
