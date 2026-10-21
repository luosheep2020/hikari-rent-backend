package com.hikari.rent.user.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(

  @Size(
    min = 1,
    max = 20,
    message = "Nickname must contain 1 to 20 characters"
  )
  String nickname,

  @Positive(message = "Avatar file ID must be positive")
  Long avatarFileId,

  Boolean removeAvatar

) {

  @AssertTrue(message = "At least one profile field is required")
  public boolean isUpdatePresent() {
    return nickname != null
      || avatarFileId != null
      || Boolean.TRUE.equals(removeAvatar);
  }

  @AssertTrue(
    message = "Cannot set and remove an avatar at the same time"
  )
  public boolean isAvatarSelectionValid() {
    return avatarFileId == null
      || !Boolean.TRUE.equals(removeAvatar);
  }
}
