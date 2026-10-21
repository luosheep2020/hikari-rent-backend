package com.hikari.rent.auth.controller;

import com.hikari.rent.auth.dto.LoginRequest;
import com.hikari.rent.auth.dto.RegisterRequest;
import com.hikari.rent.auth.service.AuthService;
import com.hikari.rent.auth.vo.AuthUserVO;
import com.hikari.rent.auth.vo.LoginVO;
import com.hikari.rent.common.result.ApiResponse;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  @Resource
  private AuthService authService;

  @PostMapping("/login")
  public ResponseEntity<ApiResponse<LoginVO>> login(
    @Valid @RequestBody LoginRequest request
  ) {
    LoginVO result = authService.login(request);

    return ResponseEntity.ok()
      .cacheControl(CacheControl.noStore())
      .body(ApiResponse.success(result));
  }

  @GetMapping("/me")
  public ResponseEntity<ApiResponse<AuthUserVO>> me() {
    AuthUserVO user = authService.me();

    return ResponseEntity.ok()
      .cacheControl(CacheControl.noStore())
      .body(ApiResponse.success(user));
  }

  @PostMapping("/logout")
  public ResponseEntity<ApiResponse<Void>> logout() {
    authService.logout();

    return ResponseEntity.ok()
      .cacheControl(CacheControl.noStore())
      .body(ApiResponse.success());
  }

  @PostMapping("/register")
  public ResponseEntity<ApiResponse<Void>> register(
    @Valid @RequestBody RegisterRequest request
  ) {
    authService.register(request);

    return ResponseEntity.ok()
      .cacheControl(CacheControl.noStore())
      .body(ApiResponse.success());
  }
}
