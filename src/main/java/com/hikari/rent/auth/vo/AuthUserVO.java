package com.hikari.rent.auth.vo;

import java.util.List;

public record AuthUserVO(
  String id,
  String username,
  String email,
  String nickname,
  String phone,
  String avatar,
  String companyId,
  List<String> roles
) {
}
