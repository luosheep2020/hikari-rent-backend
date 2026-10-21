package com.hikari.rent.auth.repository.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CurrentUserRoleRow {

  private Long userId;

  private Long companyId;

  private Integer companyStatus;

  private String roleCode;

  private String roleScope;
}
