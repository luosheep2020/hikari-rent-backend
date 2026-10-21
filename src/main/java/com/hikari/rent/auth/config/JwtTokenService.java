package com.hikari.rent.auth.service;

import com.hikari.rent.auth.config.JwtProperties;
import jakarta.annotation.Resource;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class JwtTokenService {

  @Resource
  private JwtEncoder jwtEncoder;

  @Resource
  private JwtProperties jwtProperties;

  public IssuedToken issue(Long userId) {
    if (userId == null || userId <= 0) {
      throw new IllegalArgumentException(
        "A valid user ID is required"
      );
    }

    Instant issuedAt = Instant.now()
      .truncatedTo(ChronoUnit.SECONDS);

    Instant expiresAt = issuedAt.plus(
      jwtProperties.accessTtl()
    );

    String sessionId = UUID.randomUUID().toString();

    JwtClaimsSet claims = JwtClaimsSet.builder()
      .issuer(jwtProperties.issuer())
      .subject(userId.toString())
      .id(sessionId)
      .issuedAt(issuedAt)
      .notBefore(issuedAt)
      .expiresAt(expiresAt)
      .build();

    JwsHeader header = JwsHeader
      .with(MacAlgorithm.HS256)
      .build();

    Jwt jwt = jwtEncoder.encode(
      JwtEncoderParameters.from(
        header,
        claims
      )
    );

    return new IssuedToken(
      jwt.getTokenValue(),
      sessionId,
      issuedAt,
      expiresAt
    );
  }

  public record IssuedToken(
    String accessToken,
    String sessionId,
    Instant issuedAt,
    Instant expiresAt
  ) {
  }
}
