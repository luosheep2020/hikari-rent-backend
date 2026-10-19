package com.hikari.rent.user.task;

import com.hikari.rent.user.service.BrowsingHistoryService;

import jakarta.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BrowsingHistorySyncTask {

  @Resource
  private BrowsingHistoryService browsingHistoryService;

  @Scheduled(
    initialDelayString =
      "${app.history.sync.initial-delay-ms:300000}",
    fixedDelayString =
      "${app.history.sync.fixed-delay-ms:300000}"
  )
  public void synchronize() {

    try {
      browsingHistoryService.synchronizePending();
    } catch (Exception exception) {

      log.warn(
        "Failed to execute history synchronization",
        exception
      );
    }
  }
}
