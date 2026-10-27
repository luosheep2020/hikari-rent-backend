package com.hikari.rent.auth.vo;

public record AdminRoleVO(
  String id,
  String code,
  String name,
  String description,
  String scope,
  Integer status
) {
}
