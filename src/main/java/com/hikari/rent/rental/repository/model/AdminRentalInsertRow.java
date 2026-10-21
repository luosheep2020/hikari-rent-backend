package com.hikari.rent.rental.repository.model;

import com.hikari.rent.rental.dto.admin.AdminRentalCreateRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminRentalInsertRow {

  private Long id;

  private Long companyId;

  private Long agentId;

  private Long operatorId;

  private AdminRentalCreateRequest values;
}
