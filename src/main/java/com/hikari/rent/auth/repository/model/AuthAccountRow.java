package com.hikari.rent.auth.repository.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AuthAccountRow {

  private Long id;

  private String email;

  private String passwordHash;

  private String nickname;

  private String phone;

  private String avatarStorageKey;

  private Long companyId;

  private Integer status;

  private LocalDateTime passwordChangedAt;
}
