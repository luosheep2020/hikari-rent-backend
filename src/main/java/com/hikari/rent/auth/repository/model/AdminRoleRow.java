package com.hikari.rent.auth.repository.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminRoleRow {

  private Long id;

  private String code;

  private String name;

  private String description;

  private String scope;

  private Integer status;
}
