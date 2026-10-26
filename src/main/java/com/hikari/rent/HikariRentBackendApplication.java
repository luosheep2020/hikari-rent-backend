package com.hikari.rent;

import com.hikari.rent.rental.search.service.RentalIndexService;
import jakarta.annotation.Resource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class HikariRentBackendApplication {
  @Resource
  private RentalIndexService rentalIndexService;
  public static void main(String[] args) {
    SpringApplication.run(HikariRentBackendApplication.class, args);

  }

}
