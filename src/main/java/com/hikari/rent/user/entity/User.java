package com.hikari.rent.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@TableName("users")
public class User {

  @TableId(type = IdType.AUTO)
  private Long id;

  private String email;

  private String passwordHash;

  private String nickname;

  private Long avatarFileId;

  private String phone;

  private Long companyId;

  private Long departmentId;

  private Long positionId;

  private Integer status;

  private LocalDateTime emailVerifiedAt;

  private LocalDateTime lastLoginAt;

  private LocalDateTime passwordChangedAt;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;
}
