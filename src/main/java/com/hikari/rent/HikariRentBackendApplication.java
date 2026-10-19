package com.hikari.rent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class HikariRentBackendApplication {

  public static void main(String[] args) {
    SpringApplication.run(HikariRentBackendApplication.class, args);
  }

}
