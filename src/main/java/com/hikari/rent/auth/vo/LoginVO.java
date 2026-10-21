package com.hikari.rent.auth.vo;

import java.time.Instant;

public record LoginVO(
  String accessToken,
  String tokenType,
  Instant expiresAt,
  AuthUserVO user
) {
}
