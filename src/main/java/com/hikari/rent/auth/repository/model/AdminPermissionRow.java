package com.hikari.rent.auth.repository.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminPermissionRow {

  private Long id;

  private String code;

  private String name;

  private String module;

  private String description;
}
