package com.hikari.rent.company.repository;

import com.hikari.rent.company.entity.Position;

import java.util.List;

public interface AdminPositionRepository {

  List<Position> findByCompanyId(Long companyId);

  Position findById(Long positionId);

  Position findByIdForUpdate(
    Long positionId,
    Long companyId
  );

  void insert(Position position);

  void update(Position position);

  void updateStatus(
    Long positionId,
    Long companyId,
    int status
  );

  boolean hasActiveUsers(
    Long positionId,
    Long companyId
  );
}
