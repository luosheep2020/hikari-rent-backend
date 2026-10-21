package com.hikari.rent.file.repository;

import com.hikari.rent.file.repository.model.FileUploadRow;

public interface FileRepository {

  int create(FileUploadRow file);
}
