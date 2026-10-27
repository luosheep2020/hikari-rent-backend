package com.hikari.rent.railway.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StationSearchRequest {

  @Size(
    max = 100,
    message = "Keyword must not exceed 100 characters"
  )
  private String keyword;

  @Pattern(
    regexp = "^(0[1-9]|[1-3][0-9]|4[0-7])$",
    message = "Prefecture code must be between 01 and 47"
  )
  private String prefectureCode;

  @Min(value = 1, message = "Limit must be at least 1")
  @Max(value = 20, message = "Limit must not exceed 20")
  private Integer limit = 20;
}
