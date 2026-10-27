package com.hikari.rent.auth.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AdminUserRolesRequest {

  @NotNull
  @Size(min = 1, max = 4)
  private List<@NotNull @Positive Long> roleIds;
}
