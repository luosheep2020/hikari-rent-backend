package com.hikari.rent.area.mapper;

import com.hikari.rent.area.entity.Area;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminAreaMapper {

  List<Area> selectAllOrdered();

  Area selectByCode(
    @Param("code") String code
  );

  Area selectByCodeForUpdate(
    @Param("code") String code
  );

  Area selectByCodeForShare(
    @Param("code") String code
  );

  int insert(
    @Param("area") Area area
  );

  int update(
    @Param("area") Area area
  );
}
