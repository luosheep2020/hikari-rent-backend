package com.hikari.rent.file.service;

import com.hikari.rent.file.enums.FilePurpose;
import com.hikari.rent.file.vo.FileUploadVO;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {

  FileUploadVO upload(
    MultipartFile file,
    FilePurpose purpose
  );
}
