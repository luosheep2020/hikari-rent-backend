package com.hikari.rent.user.mapper;

import com.hikari.rent.user.repository.model.UserProfileRow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserProfileMapper {

  UserProfileRow selectProfile(
    @Param("userId") Long userId
  );

  Long selectOwnedAvatarForShare(
    @Param("userId") Long userId,
    @Param("fileId") Long fileId
  );

  int updateProfile(
    @Param("userId") Long userId,
    @Param("nickname") String nickname,
    @Param("avatarFileId") Long avatarFileId,
    @Param("removeAvatar") boolean removeAvatar
  );
}
