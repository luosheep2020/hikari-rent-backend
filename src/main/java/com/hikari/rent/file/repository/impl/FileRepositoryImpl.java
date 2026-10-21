package com.hikari.rent.file.repository.impl;

import com.hikari.rent.file.mapper.FileMapper;
import com.hikari.rent.file.repository.FileRepository;
import com.hikari.rent.file.repository.model.FileUploadRow;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Repository;

@Repository
public class FileRepositoryImpl implements FileRepository {

  @Resource
  private FileMapper fileMapper;

  @Override
  public int create(FileUploadRow file) {
    return fileMapper.insertFile(file);
  }
}
