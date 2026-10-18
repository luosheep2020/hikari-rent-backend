package com.hikari.rent.area.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hikari.rent.area.entity.Area;
import com.hikari.rent.area.repository.model.AreaPopularityRow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AreaMapper extends BaseMapper<Area> {
  List<AreaPopularityRow> selectPopularityAreasByCodes(
    @Param("codes") List<String> codes
  );}
