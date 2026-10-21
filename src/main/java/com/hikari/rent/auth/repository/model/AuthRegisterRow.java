package com.hikari.rent.auth.repository.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRegisterRow {

  private Long id;

  private String email;

  private String passwordHash;

  private String nickname;
}
