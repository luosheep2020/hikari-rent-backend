package com.hikari.rent.rental.search.job;

import com.hikari.rent.rental.search.service.RentalIndexSyncTaskService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(
  name = "app.elasticsearch.sync.enabled",
  havingValue = "true"
)
public class RentalIndexSyncJob {

  @Resource
  private RentalIndexSyncTaskService taskService;

  @Scheduled(
    initialDelayString =
      "${app.elasticsearch.sync.initial-delay-ms:10000}",
    fixedDelayString =
      "${app.elasticsearch.sync.fixed-delay-ms:3000}"
  )
  public void execute() {
    for (int i = 0; i < 10; i++) {
      try {
        if (!taskService.processNext()) {
          return;
        }

      } catch (RuntimeException ex) {
        log.error(
          "Failed to process rental index sync tasks",
          ex
        );

        return;
      }
    }
  }
}
