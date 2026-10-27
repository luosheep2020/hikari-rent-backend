package com.hikari.rent.railway.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RailwayLine {

  private Long id;

  private String sourceCode;

  private String name;

  private String nameKana;

  private String operatorSourceCode;

  private String operatorName;

  private String color;

  private Integer status;

  private Integer sortOrder;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;
}
