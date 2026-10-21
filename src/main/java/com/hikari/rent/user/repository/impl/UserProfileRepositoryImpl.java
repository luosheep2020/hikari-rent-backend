package com.hikari.rent.user.repository.impl;

import com.hikari.rent.user.mapper.UserProfileMapper;
import com.hikari.rent.user.repository.UserProfileRepository;
import com.hikari.rent.user.repository.model.UserProfileRow;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Repository;

@Repository
public class UserProfileRepositoryImpl
  implements UserProfileRepository {

  @Resource
  private UserProfileMapper userProfileMapper;

  @Override
  public UserProfileRow findProfile(Long userId) {
    return userProfileMapper.selectProfile(userId);
  }

  @Override
  public Long findOwnedAvatarForShare(
    Long userId,
    Long fileId
  ) {
    return userProfileMapper.selectOwnedAvatarForShare(
      userId,
      fileId
    );
  }

  @Override
  public int updateProfile(
    Long userId,
    String nickname,
    Long avatarFileId,
    boolean removeAvatar
  ) {
    return userProfileMapper.updateProfile(
      userId,
      nickname,
      avatarFileId,
      removeAvatar
    );
  }
}
