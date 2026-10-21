package com.hikari.rent.user.controller;

import com.hikari.rent.common.result.ApiResponse;
import com.hikari.rent.user.dto.UpdateProfileRequest;
import com.hikari.rent.user.service.UserProfileService;
import com.hikari.rent.user.vo.UserProfileVO;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user/account/profile")
public class UserProfileController {

  @Resource
  private UserProfileService userProfileService;

  @GetMapping
  public ResponseEntity<ApiResponse<UserProfileVO>> getProfile() {
    return ResponseEntity.ok()
      .cacheControl(CacheControl.noStore())
      .body(
        ApiResponse.success(
          userProfileService.getProfile()
        )
      );
  }

  @PatchMapping
  public ResponseEntity<ApiResponse<UserProfileVO>> updateProfile(
    @Valid @RequestBody UpdateProfileRequest request
  ) {
    return ResponseEntity.ok()
      .cacheControl(CacheControl.noStore())
      .body(
        ApiResponse.success(
          userProfileService.updateProfile(request)
        )
      );
  }
}
