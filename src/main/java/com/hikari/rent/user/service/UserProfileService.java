package com.hikari.rent.user.service;

import com.hikari.rent.user.dto.UpdateProfileRequest;
import com.hikari.rent.user.vo.UserProfileVO;

public interface UserProfileService {

  UserProfileVO getProfile();

  UserProfileVO updateProfile(
    UpdateProfileRequest request
  );
}
