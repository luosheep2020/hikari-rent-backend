package com.hikari.rent.rental.repository.model;

import com.hikari.rent.rental.dto.admin.AdminRentalUpdateRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminRentalUpdateRow {

  private Long id;

  private Long companyId;

  private Long agentId;

  private Long operatorId;

  private AdminRentalUpdateRequest values;
}
