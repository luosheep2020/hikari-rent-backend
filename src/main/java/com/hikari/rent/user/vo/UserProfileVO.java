package com.hikari.rent.user.vo;

public record UserProfileVO(
  String id,
  String nickname,
  String avatar,
  String avatarFileId
) {
}
