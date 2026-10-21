package com.hikari.rent.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(

  @NotBlank(message = "Email is required")
  @Email(message = "Invalid email format")
  @Size(max = 254, message = "Email must not exceed 254 characters")
  String email,

  @NotBlank(message = "Password is required")
  @Size(max = 20, message = "Password must not exceed 20 characters")
  String password

) {
}
