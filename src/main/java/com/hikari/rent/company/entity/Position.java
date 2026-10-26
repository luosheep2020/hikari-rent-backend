package com.hikari.rent.company.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Position {

  private Long id;

  private Long companyId;

  private String name;

  private String code;

  private Integer sortOrder;

  private Integer status;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;
}
