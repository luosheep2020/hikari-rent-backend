package com.hikari.rent.auth.exception;

import com.hikari.rent.common.result.ApiResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class AuthExceptionHandler {

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<ApiResponse<Void>> handleAuthentication(
    AuthenticationException exception
  ) {
    if (exception instanceof AuthenticationServiceException) {
      return ResponseEntity
        .status(HttpStatus.SERVICE_UNAVAILABLE)
        .cacheControl(CacheControl.noStore())
        .body(
          ApiResponse.failure(
            503,
            "Authentication service is temporarily unavailable"
          )
        );
    }

    return ResponseEntity
      .status(HttpStatus.UNAUTHORIZED)
      .cacheControl(CacheControl.noStore())
      .body(
        ApiResponse.failure(
          401,
          "Invalid email or password"
        )
      );
  }

  @ExceptionHandler(EmailAlreadyRegisteredException.class)
  public ResponseEntity<ApiResponse<Void>> handleEmailAlreadyRegistered(
    EmailAlreadyRegisteredException exception
  ) {
    return ResponseEntity
      .status(HttpStatus.CONFLICT)
      .cacheControl(CacheControl.noStore())
      .body(
        ApiResponse.failure(
          409,
          exception.getMessage()
        )
      );
  }
}
