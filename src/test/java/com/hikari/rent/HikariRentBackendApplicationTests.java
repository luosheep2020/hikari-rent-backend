package com.hikari.rent;

import com.hikari.rent.rental.search.service.RentalIndexService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
  properties = {
    "spring.config.import=file:./.env[.properties]"
  }
)
class HikariRentBackendApplicationTests {

  @Resource
  private RentalIndexService rentalIndexService;

  @Test
  void contextLoads() {
  }

  @Test
  @EnabledIfEnvironmentVariable(
    named = "RUN_RENTAL_INDEX_IMPORT",
    matches = "true"
  )
  void importPublicRentals() {
    var result =
      rentalIndexService.importPublicRentals();

    System.out.printf(
      "Import completed: processed=%d, lastRentalId=%d, upperRentalId=%d%n",
      result.processedCount(),
      result.lastRentalId(),
      result.upperRentalId()
    );
  }
}
