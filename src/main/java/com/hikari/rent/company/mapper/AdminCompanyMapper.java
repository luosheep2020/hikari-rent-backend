package com.hikari.rent.company.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hikari.rent.company.dto.AdminCompanyPageRequest;
import com.hikari.rent.company.entity.Company;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AdminCompanyMapper {

  IPage<Company> searchPage(
    IPage<Company> page,
    @Param("query") AdminCompanyPageRequest query
  );

  Company selectById(
    @Param("companyId") Long companyId
  );

  Company selectByIdForUpdate(
    @Param("companyId") Long companyId
  );

  int insert(
    @Param("company") Company company
  );

  int update(
    @Param("company") Company company
  );

  int updateStatus(
    @Param("companyId") Long companyId,
    @Param("status") int status
  );

  int enqueueRelatedRentalSyncTasks(
    @Param("companyId") Long companyId
  );
}
