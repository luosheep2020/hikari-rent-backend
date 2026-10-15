package com.hikari.rent.area.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@TableName("areas")
public class Area {

  @TableId(type = IdType.INPUT)
  private String code;

  private String name;

  private String parentCode;

  private Integer level;

  private BigDecimal latitude;

  private BigDecimal longitude;

  private Integer sortOrder;
}
