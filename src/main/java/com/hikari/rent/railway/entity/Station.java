package com.hikari.rent.railway.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@TableName("stations")
public class Station {

  @TableId(type = IdType.AUTO)
  private Long id;

  private String sourceCode;

  private String stationGroupCode;

  private String name;

  private String nameKana;

  private String lineName;

  private Long lineId;

  private String prefectureCode;

  private String areaCode;

  private String address;

  private String postalCode;

  private BigDecimal latitude;

  private BigDecimal longitude;

  private Integer status;

  private Integer sortOrder;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;
}
