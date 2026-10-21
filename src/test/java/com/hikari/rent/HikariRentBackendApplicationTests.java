package com.hikari.rent;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

class HikariRentBackendApplicationTests {

  @Test
  void generatePasswordHash() {
    var encoder = new BCryptPasswordEncoder();

    String hash = encoder.encode(
      "12345678"
    );

    System.out.println(hash);
  }

}
