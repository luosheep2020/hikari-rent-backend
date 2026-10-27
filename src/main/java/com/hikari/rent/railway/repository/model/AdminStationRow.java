package com.hikari.rent.railway.repository.model;

import com.hikari.rent.railway.entity.Station;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminStationRow extends Station {

  private String operatorSourceCode;

  private String operatorName;

  private String prefectureName;

  private String areaName;
}
