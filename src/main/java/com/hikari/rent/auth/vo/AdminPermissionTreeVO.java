package com.hikari.rent.auth.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class AdminPermissionTreeVO {

  private String key;

  private String title;

  private String module;

  private String permissionId;

  private String code;

  private String description;

  private List<AdminPermissionTreeVO> children =
    new ArrayList<>();
}
