package com.hikari.rent.company.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminPositionSaveRequest {

  @Positive
  private Long companyId;

  @NotBlank
  @Size(max = 100)
  private String name;

  @NotBlank
  @Pattern(
    regexp = "^[A-Za-z0-9_-]{1,50}$",
    message = "Position code must contain letters, digits, underscores or hyphens"
  )
  private String code;

  @NotNull
  @Min(0)
  private Integer sortOrder = 0;
}
