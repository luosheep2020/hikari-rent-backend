package com.hikari.rent.company.mapper;

import com.hikari.rent.company.entity.Position;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminPositionMapper {

  List<Position> selectByCompanyId(
    @Param("companyId") Long companyId
  );

  Position selectById(
    @Param("positionId") Long positionId
  );

  Position selectByIdForUpdate(
    @Param("positionId") Long positionId,
    @Param("companyId") Long companyId
  );

  int insert(
    @Param("position") Position position
  );

  int update(
    @Param("position") Position position
  );

  int updateStatus(
    @Param("positionId") Long positionId,
    @Param("companyId") Long companyId,
    @Param("status") int status
  );

  int countActiveUsers(
    @Param("positionId") Long positionId,
    @Param("companyId") Long companyId
  );
}
