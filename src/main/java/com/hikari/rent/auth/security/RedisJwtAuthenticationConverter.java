package com.hikari.rent.auth.security;

import com.hikari.rent.auth.cache.LoginSessionCache;
import com.hikari.rent.auth.model.AuthPrincipal;
import com.hikari.rent.auth.model.LoginSession;
import com.hikari.rent.auth.repository.AuthAccountRepository;
import com.hikari.rent.auth.repository.model.AuthAccountRow;
import com.hikari.rent.auth.service.AuthIdentityService;
import com.hikari.rent.auth.vo.AuthUserVO;
import jakarta.annotation.Resource;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Component
public class RedisJwtAuthenticationConverter
  implements Converter<Jwt, AbstractAuthenticationToken> {

  @Resource
  private LoginSessionCache loginSessionCache;

  @Resource
  private AuthAccountRepository authAccountRepository;

  @Resource
  private AuthIdentityService authIdentityService;

  @Override
  public AbstractAuthenticationToken convert(Jwt jwt) {
    Long userId = parseUserId(jwt.getSubject());

    String sessionId = validateSessionId(
      jwt.getId()
    );

    try {
      LoginSession session =
        loginSessionCache.find(sessionId);

      validateSession(
        jwt,
        session,
        userId
      );

      AuthAccountRow account =
        authAccountRepository.findById(userId);

      validateAccount(
        account,
        session
      );

      // Load current roles instead of using the cached role snapshot.
      AuthUserVO user =
        authIdentityService.toUserVO(account);

      var authorities = user.roles()
        .stream()
        .map(role ->
          new SimpleGrantedAuthority(
            "ROLE_" + role
          )
        )
        .toList();

      AuthPrincipal principal = new AuthPrincipal(
        userId,
        sessionId,
        user
      );

      return UsernamePasswordAuthenticationToken
        .authenticated(
          principal,
          null,
          authorities
        );

    } catch (InvalidBearerTokenException ex) {
      throw ex;

    } catch (RuntimeException ex) {
      // Infrastructure failures must not authenticate the request.
      throw new OAuth2AuthenticationException(
        new OAuth2Error("temporarily_unavailable"),
        "Authentication service is temporarily unavailable",
        ex
      );
    }
  }

  private Long parseUserId(String subject) {
    try {
      Long userId = Long.valueOf(subject);

      if (userId <= 0) {
        throw new IllegalArgumentException();
      }

      return userId;

    } catch (RuntimeException ex) {
      throw new InvalidBearerTokenException(
        "Invalid token subject"
      );
    }
  }

  private String validateSessionId(String sessionId) {
    try {
      if (
        sessionId == null
          || !UUID.fromString(sessionId)
          .toString()
          .equals(sessionId)
      ) {
        throw new IllegalArgumentException();
      }

      return sessionId;

    } catch (RuntimeException ex) {
      throw new InvalidBearerTokenException(
        "Invalid token session ID"
      );
    }
  }

  private void validateSession(
    Jwt jwt,
    LoginSession session,
    Long userId
  ) {
    if (
      session == null
        || !userId.toString().equals(session.userId())
        || session.expiresAt() == null
        || !session.expiresAt().isAfter(Instant.now())
        || !LoginSessionCache.matches(
        jwt.getTokenValue(),
        session.tokenHash()
      )
    ) {
      throw new InvalidBearerTokenException(
        "Login session has expired or was revoked"
      );
    }
  }

  private void validateAccount(
    AuthAccountRow account,
    LoginSession session
  ) {
    if (
      account == null
        || !Integer.valueOf(1).equals(account.getStatus())
        || !Objects.equals(
        account.getPasswordChangedAt(),
        session.passwordChangedAt()
      )
    ) {
      throw new InvalidBearerTokenException(
        "Login session is no longer valid"
      );
    }
  }
}
