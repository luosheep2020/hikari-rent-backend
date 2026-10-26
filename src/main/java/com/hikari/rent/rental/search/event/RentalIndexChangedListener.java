package com.hikari.rent.rental.search.event;

import com.hikari.rent.rental.search.service.RentalIndexService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
public class RentalIndexChangedListener {

  @Resource
  private RentalIndexService rentalIndexService;

  @TransactionalEventListener(
    phase = TransactionPhase.AFTER_COMMIT
  )
  public void handle(
    RentalIndexChangedEvent event
  ) {
    try {
      rentalIndexService.syncRental(
        event.rentalId()
      );

    } catch (Exception ex) {
      // The database transaction has already committed.
      log.error(
        "Rental index synchronization failed, rentalId={}",
        event.rentalId(),
        ex
      );
    }
  }
}
