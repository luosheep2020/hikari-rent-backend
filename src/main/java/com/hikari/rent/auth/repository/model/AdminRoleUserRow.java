package com.hikari.rent.auth.repository.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminRoleUserRow {

  private Long id;

  private Long companyId;

  private Integer status;
}
