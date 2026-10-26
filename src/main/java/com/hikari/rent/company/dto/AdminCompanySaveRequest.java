package com.hikari.rent.company.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminCompanySaveRequest {

  @NotBlank
  @Size(max = 150)
  private String name;

  @Size(max = 10)
  private String postalCode;

  @NotBlank
  @Size(max = 255)
  private String address;

  @Size(max = 25)
  private String phone;

  @Email
  @Size(max = 254)
  private String email;

  @Size(max = 100)
  private String licenseNumber;

  @Size(max = 100)
  private String businessHours;

  @Size(max = 100)
  private String closedDays;
}
