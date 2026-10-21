package com.hikari.rent.user.service.impl;

import com.hikari.rent.auth.service.CurrentUserService;
import com.hikari.rent.common.enums.ErrorCode;
import com.hikari.rent.common.exception.BusinessException;
import com.hikari.rent.file.service.FileUrlService;
import com.hikari.rent.user.dto.UpdateProfileRequest;
import com.hikari.rent.user.repository.UserProfileRepository;
import com.hikari.rent.user.repository.model.UserProfileRow;
import com.hikari.rent.user.service.UserProfileService;
import com.hikari.rent.user.vo.UserProfileVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserProfileServiceImpl
  implements UserProfileService {

  @Resource
  private CurrentUserService currentUserService;

  @Resource
  private UserProfileRepository userProfileRepository;

  @Resource
  private FileUrlService fileUrlService;

  @Override
  @Transactional(readOnly = true)
  public UserProfileVO getProfile() {
    Long userId = currentUserService.requireUserId();

    return loadProfile(userId);
  }

  @Override
  @Transactional
  public UserProfileVO updateProfile(
    UpdateProfileRequest request
  ) {
    Long userId = currentUserService.requireUserId();

    String nickname = request.nickname();

    if (nickname != null) {
      nickname = nickname.strip();

      if (nickname.isBlank()) {
        throw new BusinessException(
          ErrorCode.BAD_REQUEST
        );
      }
    }

    Long avatarFileId = request.avatarFileId();

    if (avatarFileId != null) {
      Long validFileId =
        userProfileRepository.findOwnedAvatarForShare(
          userId,
          avatarFileId
        );

      if (validFileId == null) {
        throw new BusinessException(
          ErrorCode.BAD_REQUEST
        );
      }
    }

    userProfileRepository.updateProfile(
      userId,
      nickname,
      avatarFileId,
      Boolean.TRUE.equals(request.removeAvatar())
    );

    return loadProfile(userId);
  }

  private UserProfileVO loadProfile(Long userId) {
    UserProfileRow profile =
      userProfileRepository.findProfile(userId);

    if (profile == null) {
      throw new BusinessException(
        ErrorCode.AUTHENTICATION_REQUIRED
      );
    }

    String avatar = profile.getAvatarStorageKey() == null
      ? null
      : fileUrlService.getPublicUrl(
      profile.getAvatarStorageKey()
    );

    return new UserProfileVO(
      profile.getId().toString(),
      profile.getNickname(),
      avatar,
      profile.getAvatarFileId() == null
        ? null
        : profile.getAvatarFileId().toString()
    );
  }
}
