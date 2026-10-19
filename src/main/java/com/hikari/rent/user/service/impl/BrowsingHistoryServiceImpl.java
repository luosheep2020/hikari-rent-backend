package com.hikari.rent.user.service.impl;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import com.hikari.rent.auth.service.CurrentUserService;
import com.hikari.rent.common.enums.ErrorCode;
import com.hikari.rent.common.exception.BusinessException;

import com.hikari.rent.rental.service.RentalService;
import com.hikari.rent.rental.vo.RentalListVO;

import com.hikari.rent.user.cache.BrowsingHistoryCache;
import com.hikari.rent.user.cache.BrowsingHistoryCache.HistorySnapshot;
import com.hikari.rent.user.repository.BrowsingHistoryRepository;
import com.hikari.rent.user.repository.model.BrowsingHistoryRow;
import com.hikari.rent.user.service.BrowsingHistoryService;

import jakarta.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BrowsingHistoryServiceImpl
  implements BrowsingHistoryService {

  private static final int LOCK_COUNT = 256;

  /*
   * Bounded locks serialize operations for each user
   * within the current application instance.
   */
  private final ReentrantLock[] userLocks =
    IntStream.range(0, LOCK_COUNT)
      .mapToObj(index -> new ReentrantLock())
      .toArray(ReentrantLock[]::new);

  private final AtomicBoolean synchronizing =
    new AtomicBoolean(false);

  @Resource
  private CurrentUserService currentUserService;

  @Resource
  private BrowsingHistoryCache browsingHistoryCache;

  @Resource
  private BrowsingHistoryRepository browsingHistoryRepository;

  @Resource
  private RentalService rentalService;

  @Override
  public List<RentalListVO> getHistory() {

    Long userId = currentUserService.requireUserId();

    List<Long> rentalIds = withUserLock(
      userId,
      () -> loadSnapshot(userId)
        .records()
        .stream()
        .map(BrowsingHistoryRow::getRentalId)
        .toList()
    );

    if (rentalIds.isEmpty()) {
      return List.of();
    }

    // Preserve history order and hide unavailable rentals.
    return rentalService.getPublicListByIds(rentalIds);
  }

  @Override
  public void recordView(Long rentalId) {

    validateRentalId(rentalId);

    Long userId = currentUserService.requireUserId();

    // Validate public availability before recording the view.
    if (
      rentalService.getPublicListByIds(
        List.of(rentalId)
      ).isEmpty()
    ) {
      throw new BusinessException(
        ErrorCode.RENTAL_NOT_FOUND
      );
    }

    withUserLock(userId, () -> {

      loadSnapshot(userId);

      browsingHistoryCache.recordView(
        userId,
        rentalId
      );

      return null;
    });
  }

  @Override
  public void remove(Long rentalId) {

    validateRentalId(rentalId);

    Long userId = currentUserService.requireUserId();

    withUserLock(userId, () -> {

      loadSnapshot(userId);

      // Unavailable rentals must also remain removable.
      browsingHistoryCache.remove(
        userId,
        rentalId
      );

      return null;
    });
  }

  @Override
  public void clear() {

    Long userId = currentUserService.requireUserId();

    withUserLock(userId, () -> {

      loadSnapshot(userId);

      browsingHistoryCache.clear(userId);

      return null;
    });
  }

  @Override
  public void synchronizePending() {

    if (!synchronizing.compareAndSet(false, true)) {
      return;
    }

    try {
      for (
        Long userId :
        browsingHistoryCache.findDirtyUserIds()
      ) {
        synchronizeUser(userId);
      }
    } finally {
      synchronizing.set(false);
    }
  }

  private void synchronizeUser(Long userId) {

    ReentrantLock lock = getUserLock(userId);

    // Retry busy users during the next synchronization round.
    if (!lock.tryLock()) {
      return;
    }

    try {
      HistorySnapshot snapshot =
        browsingHistoryCache.readSnapshot(userId)
          .orElseThrow(() ->
            new IllegalStateException(
              "Dirty history cache is not initialized"
            )
          );

      /*
       * The repository transaction commits before this call returns.
       * Do not acknowledge Redis before the commit completes.
       */
      browsingHistoryRepository.replaceSnapshot(
        userId,
        snapshot.records()
      );

      boolean acknowledged =
        browsingHistoryCache.acknowledge(
          userId,
          snapshot.version()
        );

      log.debug(
        "History synchronized: userId={}, count={}, acknowledged={}",
        userId,
        snapshot.records().size(),
        acknowledged
      );

    } catch (Exception exception) {

      // Keep the dirty marker so the next round can retry.
      log.warn(
        "Failed to synchronize history: userId={}",
        userId,
        exception
      );

    } finally {
      lock.unlock();
    }
  }

  private HistorySnapshot loadSnapshot(Long userId) {

    var cached =
      browsingHistoryCache.readSnapshot(userId);

    if (cached.isPresent()) {
      return cached.get();
    }

    List<BrowsingHistoryRow> records =
      browsingHistoryRepository.findRecent(userId);

    browsingHistoryCache.initializeIfAbsent(
      userId,
      records
    );

    return browsingHistoryCache.readSnapshot(userId)
      .orElseThrow(() ->
        new IllegalStateException(
          "History cache initialization failed"
        )
      );
  }

  private <T> T withUserLock(
    Long userId,
    Supplier<T> action
  ) {

    ReentrantLock lock = getUserLock(userId);

    lock.lock();

    try {
      return action.get();
    } finally {
      lock.unlock();
    }
  }

  private ReentrantLock getUserLock(Long userId) {

    int index = Math.floorMod(
      userId.hashCode(),
      LOCK_COUNT
    );

    return userLocks[index];
  }

  private void validateRentalId(Long rentalId) {

    if (rentalId == null || rentalId <= 0) {
      throw new BusinessException(
        ErrorCode.BAD_REQUEST
      );
    }
  }
}
