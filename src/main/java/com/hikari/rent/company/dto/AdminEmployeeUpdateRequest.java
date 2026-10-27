package com.hikari.rent.company.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminEmployeeUpdateRequest {

  @NotBlank
  @Size(max = 32)
  private String nickname;

  @Size(max = 25)
  private String phone;

  @Positive
  private Long departmentId;

  @Positive
  private Long positionId;
}
