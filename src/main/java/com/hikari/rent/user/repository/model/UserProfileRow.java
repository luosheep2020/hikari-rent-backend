package com.hikari.rent.user.repository.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfileRow {

  private Long id;

  private String nickname;

  private Long avatarFileId;

  private String avatarStorageKey;
}
