package com.hikari.rent.file.repository.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileUploadRow {

  private Long id;

  private Long ownerId;

  private String storageKey;

  private String originalName;

  private String mimeType;

  private Long sizeBytes;

  private String purpose;
}
