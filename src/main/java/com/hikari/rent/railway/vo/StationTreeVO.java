package com.hikari.rent.railway.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class StationTreeVO {

  private String value;

  private String label;

  private List<StationTreeVO> children = new ArrayList<>();
}
