package com.hikari.rent.file.controller;

import com.hikari.rent.common.result.ApiResponse;
import com.hikari.rent.file.enums.FilePurpose;
import com.hikari.rent.file.service.FileService;
import com.hikari.rent.file.vo.FileUploadVO;
import jakarta.annotation.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
public class FileController {

  @Resource
  private FileService fileService;

  @PostMapping(
    value = "/upload",
    consumes = MediaType.MULTIPART_FORM_DATA_VALUE
  )
  public ResponseEntity<ApiResponse<FileUploadVO>> upload(
    @RequestPart("file") MultipartFile file,
    @RequestParam(defaultValue = "AVATAR") FilePurpose purpose
  ) {
    return ResponseEntity.ok()
      .cacheControl(CacheControl.noStore())
      .body(
        ApiResponse.success(
          fileService.upload(file, purpose)
        )
      );
  }
}
