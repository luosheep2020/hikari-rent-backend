package com.hikari.rent.file.mapper;

import com.hikari.rent.file.repository.model.FileUploadRow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface FileMapper {

  int insertFile(
    @Param("file") FileUploadRow file
  );
}
