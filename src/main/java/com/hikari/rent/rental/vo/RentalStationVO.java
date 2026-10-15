package com.hikari.rent.rental.vo;


import lombok.Getter;
import lombok.Setter;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

@Getter
@Setter
public class RentalStationVO {

  @JsonSerialize(using = ToStringSerializer.class)
  private Long id;

  private String name;

  private String line;

  private Integer walkingMinutes;
}
