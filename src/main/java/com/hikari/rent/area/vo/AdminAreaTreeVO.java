package com.hikari.rent.area.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class AdminAreaTreeVO extends AdminAreaVO {

  private List<AdminAreaTreeVO> children =
    new ArrayList<>();
}
