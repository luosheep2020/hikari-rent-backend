package com.hikari.rent.auth.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

  @NotBlank(message = "Nickname is required")
  @Size(max = 20, message = "Nickname must not exceed 20 characters")
  String nickname,

  @NotBlank(message = "Email is required")
  @Email(message = "Invalid email format")
  @Size(max = 254, message = "Email must not exceed 254 characters")
  String email,

  @NotBlank(message = "Password is required")
  @Size(
    min = 8,
    max = 20,
    message = "Password must contain 8 to 20 characters"
  )
  String password,

  @NotBlank(message = "Password confirmation is required")
  @Size(max = 20, message = "Password confirmation must not exceed 20 characters")
  String confirmPassword

) {

  @AssertTrue(message = "Passwords do not match")
  public boolean isPasswordConfirmed() {
    if (password == null || confirmPassword == null) {
      return true;
    }

    return password.equals(confirmPassword);
  }
}
