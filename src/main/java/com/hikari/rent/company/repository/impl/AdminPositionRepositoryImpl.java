package com.hikari.rent.company.repository.impl;

import com.hikari.rent.company.entity.Position;
import com.hikari.rent.company.mapper.AdminPositionMapper;
import com.hikari.rent.company.repository.AdminPositionRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AdminPositionRepositoryImpl
  implements AdminPositionRepository {

  @Resource
  private AdminPositionMapper adminPositionMapper;

  @Override
  public List<Position> findByCompanyId(Long companyId) {
    return adminPositionMapper.selectByCompanyId(companyId);
  }

  @Override
  public Position findById(Long positionId) {
    return adminPositionMapper.selectById(positionId);
  }

  @Override
  public Position findByIdForUpdate(
    Long positionId,
    Long companyId
  ) {
    return adminPositionMapper.selectByIdForUpdate(
      positionId,
      companyId
    );
  }

  @Override
  public void insert(Position position) {
    int inserted = adminPositionMapper.insert(position);

    if (inserted != 1 || position.getId() == null) {
      throw new IllegalStateException(
        "Position insert did not return a generated ID"
      );
    }
  }

  @Override
  public void update(Position position) {
    adminPositionMapper.update(position);
  }

  @Override
  public void updateStatus(
    Long positionId,
    Long companyId,
    int status
  ) {
    adminPositionMapper.updateStatus(
      positionId,
      companyId,
      status
    );
  }

  @Override
  public boolean hasActiveUsers(
    Long positionId,
    Long companyId
  ) {
    return adminPositionMapper.countActiveUsers(
      positionId,
      companyId
    ) > 0;
  }
}
