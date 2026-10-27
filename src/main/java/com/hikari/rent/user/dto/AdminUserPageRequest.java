package com.hikari.rent.user.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminUserPageRequest {

  @Min(1)
  private long page = 1;

  @Min(1)
  @Max(100)
  private long pageSize = 20;

  @Size(max = 100)
  private String keyword;

  @Positive
  private Long companyId;

  @Pattern(
    regexp = "USER|AGENT|COMPANY_ADMIN|PLATFORM_ADMIN"
  )
  private String roleCode;

  @Min(0)
  @Max(1)
  private Integer status;
}
