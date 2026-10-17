package com.hikari.rent.area.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.repository.CrudRepository;
import com.hikari.rent.area.entity.Area;
import com.hikari.rent.area.mapper.AreaMapper;
import com.hikari.rent.area.repository.AreaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AreaRepositoryImpl
    extends CrudRepository<AreaMapper, Area>
    implements AreaRepository {

  @Override
  public List<Area> findChildren(String parentCode) {
    LambdaQueryWrapper<Area> query=
      new LambdaQueryWrapper<>();

    if (parentCode==null){
      query.isNull(Area::getParentCode);
    }else {
      query.eq(Area::getParentCode,parentCode);
    }
    query.orderByAsc(Area::getSortOrder)
      .orderByAsc(Area::getCode);

    return baseMapper.selectList(query);
  }

  @Override
  public List<Area> findAllOrdered() {
    return baseMapper.selectList(
      new LambdaQueryWrapper<Area>()
        .orderByAsc(Area::getLevel)
        .orderByAsc(Area::getSortOrder)
        .orderByAsc(Area::getCode)
    );
  }
}
