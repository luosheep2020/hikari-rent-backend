package com.hikari.rent.auth.cache;

import com.hikari.rent.auth.model.LoginSession;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.HexFormat;

@Component
public class LoginSessionCache {

  private static final String KEY_PREFIX =
    "hikari:auth:session:";

  @Resource(name = "stringRedisTemplate")
  private StringRedisTemplate redisTemplate;

  private final JsonMapper jsonMapper =
    JsonMapper.builder().build();

  public void save(
    String sessionId,
    LoginSession session
  ) {
    Duration ttl = Duration.between(
      Instant.now(),
      session.expiresAt()
    );

    if (ttl.isNegative() || ttl.isZero()) {
      throw new IllegalArgumentException(
        "Login session has already expired"
      );
    }

    try {
      String json = jsonMapper.writeValueAsString(
        session
      );

      // Write the value and expiration in a single Redis operation.
      redisTemplate.opsForValue().set(
        buildKey(sessionId),
        json,
        ttl
      );
    } catch (Exception ex) {
      throw new AuthenticationServiceException(
        "Authentication storage is unavailable",
        ex
      );
    }
  }

  public LoginSession find(String sessionId) {
    try {
      String json = redisTemplate.opsForValue().get(
        buildKey(sessionId)
      );

      if (json == null) {
        return null;
      }

      return jsonMapper.readValue(
        json,
        LoginSession.class
      );
    } catch (Exception ex) {
      throw new AuthenticationServiceException(
        "Authentication storage is unavailable",
        ex
      );
    }
  }

  public void delete(String sessionId) {
    try {
      redisTemplate.delete(
        buildKey(sessionId)
      );
    } catch (Exception ex) {
      throw new AuthenticationServiceException(
        "Authentication storage is unavailable",
        ex
      );
    }
  }

  public static String fingerprint(String token) {
    try {
      MessageDigest digest = MessageDigest.getInstance(
        "SHA-256"
      );

      byte[] hash = digest.digest(
        token.getBytes(StandardCharsets.UTF_8)
      );

      return HexFormat.of().formatHex(hash);
    } catch (NoSuchAlgorithmException ex) {
      throw new IllegalStateException(
        "SHA-256 is unavailable",
        ex
      );
    }
  }

  public static boolean matches(
    String token,
    String expectedHash
  ) {
    if (token == null || expectedHash == null) {
      return false;
    }

    byte[] actual = fingerprint(token).getBytes(
      StandardCharsets.US_ASCII
    );

    byte[] expected = expectedHash.getBytes(
      StandardCharsets.US_ASCII
    );

    return MessageDigest.isEqual(
      actual,
      expected
    );
  }

  private String buildKey(String sessionId) {
    return KEY_PREFIX + sessionId;
  }
}
