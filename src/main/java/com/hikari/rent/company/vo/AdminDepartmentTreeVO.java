package com.hikari.rent.company.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class AdminDepartmentTreeVO {

  private String id;

  private String companyId;

  private String parentId;

  private String name;

  private String code;

  private Integer sortOrder;

  private Integer status;

  private List<AdminDepartmentTreeVO> children =
    new ArrayList<>();
}
