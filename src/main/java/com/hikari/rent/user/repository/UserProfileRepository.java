package com.hikari.rent.user.repository;

import com.hikari.rent.user.repository.model.UserProfileRow;

public interface UserProfileRepository {

  UserProfileRow findProfile(Long userId);

  Long findOwnedAvatarForShare(
    Long userId,
    Long fileId
  );

  int updateProfile(
    Long userId,
    String nickname,
    Long avatarFileId,
    boolean removeAvatar
  );
}
