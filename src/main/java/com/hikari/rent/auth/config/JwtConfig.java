package com.hikari.rent.auth.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class JwtConfig {

  @Bean
  public SecretKey jwtSecretKey(
    JwtProperties properties
  ) {
    byte[] keyBytes;

    try {
      keyBytes = Base64.getDecoder().decode(
        properties.secretBase64()
      );
    } catch (IllegalArgumentException ex) {
      throw new IllegalArgumentException(
        "JWT signing secret must be valid Base64",
        ex
      );
    }

    if (keyBytes.length < 32) {
      throw new IllegalArgumentException(
        "JWT signing secret must contain at least 32 bytes"
      );
    }

    return new SecretKeySpec(
      keyBytes,
      "HmacSHA256"
    );
  }

  @Bean
  public JwtEncoder jwtEncoder(
    SecretKey jwtSecretKey
  ) {
    return NimbusJwtEncoder
      .withSecretKey(jwtSecretKey)
      .algorithm(MacAlgorithm.HS256)
      .build();
  }

  @Bean
  public JwtDecoder jwtDecoder(
    SecretKey jwtSecretKey,
    JwtProperties properties
  ) {
    NimbusJwtDecoder decoder = NimbusJwtDecoder
      .withSecretKey(jwtSecretKey)
      .macAlgorithm(MacAlgorithm.HS256)
      .build();

    decoder.setJwtValidator(
      JwtValidators.createDefaultWithIssuer(
        properties.issuer()
      )
    );

    return decoder;
  }
}
