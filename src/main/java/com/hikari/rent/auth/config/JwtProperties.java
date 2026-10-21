package com.hikari.rent.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(
  String secretBase64,
  String issuer,
  Duration accessTtl
) {

  public JwtProperties {
    if (secretBase64 == null || secretBase64.isBlank()) {
      throw new IllegalArgumentException(
        "JWT signing secret must be configured"
      );
    }

    if (issuer == null || issuer.isBlank()) {
      throw new IllegalArgumentException(
        "JWT issuer must be configured"
      );
    }

    if (
      accessTtl == null
        || accessTtl.compareTo(Duration.ofMinutes(1)) < 0
        || accessTtl.compareTo(Duration.ofHours(24)) > 0
    ) {
      throw new IllegalArgumentException(
        "JWT access TTL must be between 1 minute and 24 hours"
      );
    }
  }
}
