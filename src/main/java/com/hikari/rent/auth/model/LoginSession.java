package com.hikari.rent.auth.model;

import com.hikari.rent.auth.vo.AuthUserVO;

import java.time.Instant;
import java.time.LocalDateTime;

public record LoginSession(
  String userId,
  String tokenHash,
  AuthUserVO user,
  Instant issuedAt,
  Instant expiresAt,
  LocalDateTime passwordChangedAt
) {
}
