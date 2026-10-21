package com.hikari.rent.file.service.impl;

import com.hikari.rent.auth.service.CurrentUserService;
import com.hikari.rent.common.enums.ErrorCode;
import com.hikari.rent.common.exception.BusinessException;
import com.hikari.rent.file.config.R2Properties;
import com.hikari.rent.file.enums.FilePurpose;
import com.hikari.rent.file.repository.FileRepository;
import com.hikari.rent.file.repository.model.FileUploadRow;
import com.hikari.rent.file.service.FileService;
import com.hikari.rent.file.service.FileUrlService;
import com.hikari.rent.file.vo.FileUploadVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.Locale;
import java.util.UUID;

@Slf4j
@Service
public class FileServiceImpl implements FileService {

  private static final long MAX_FILE_SIZE =
    10L * 1024 * 1024;

  @Resource
  private CurrentUserService currentUserService;

  @Resource
  private FileRepository fileRepository;

  @Resource
  private FileUrlService fileUrlService;

  @Resource
  private R2Properties r2Properties;

  @Resource
  private S3Client r2S3Client;

  @Resource
  private PlatformTransactionManager transactionManager;

  @Override
  public FileUploadVO upload(
    MultipartFile file,
    FilePurpose purpose
  ) {
    Long userId = currentUserService.requireUserId();

    if (
      purpose != FilePurpose.AVATAR
        || file == null
        || file.isEmpty()
        || file.getSize() > MAX_FILE_SIZE
    ) {
      throw invalidFile();
    }

    String mimeType = file.getContentType();

    if (mimeType == null) {
      throw invalidFile();
    }

    mimeType = mimeType.toLowerCase(Locale.ROOT);

    String extension = switch (mimeType) {
      case "image/jpeg" -> "jpg";
      case "image/png" -> "png";
      case "image/webp" -> "webp";
      default -> throw invalidFile();
    };

    byte[] bytes;

    try {
      bytes = file.getBytes();
    } catch (IOException exception) {
      throw invalidFile();
    }

    if (
      bytes.length == 0
        || bytes.length > MAX_FILE_SIZE
    ) {
      throw invalidFile();
    }

    String storageKey = "avatars/"
      + userId + "/"
      + UUID.randomUUID()
      + "." + extension;

    String url = fileUrlService.getPublicUrl(
      storageKey
    );

    if (url == null || url.isBlank()) {
      throw new IllegalStateException(
        "storage.public-base-url must be configured"
      );
    }

    FileUploadRow row = new FileUploadRow();
    row.setOwnerId(userId);
    row.setStorageKey(storageKey);
    row.setOriginalName(
      sanitizeFilename(
        file.getOriginalFilename(),
        extension
      )
    );
    row.setMimeType(mimeType);
    row.setSizeBytes((long) bytes.length);
    row.setPurpose(purpose.name());

    try {
      r2S3Client.putObject(
        PutObjectRequest.builder()
          .bucket(r2Properties.getBucket())
          .key(storageKey)
          .contentType(mimeType)
          .build(),
        RequestBody.fromBytes(bytes)
      );

      // Commit file metadata before returning the upload result.
      TransactionTemplate transaction =
        new TransactionTemplate(transactionManager);

      transaction.setPropagationBehavior(
        TransactionDefinition.PROPAGATION_REQUIRES_NEW
      );

      transaction.executeWithoutResult(status -> {
        int inserted = fileRepository.create(row);

        if (inserted != 1 || row.getId() == null) {
          throw new IllegalStateException(
            "Failed to save file metadata"
          );
        }
      });

    } catch (RuntimeException exception) {
      deleteObjectQuietly(storageKey);

      throw new IllegalStateException(
        "File upload failed for key: " + storageKey,
        exception
      );
    }

    return new FileUploadVO(
      row.getId().toString(),
      url
    );
  }

  private String sanitizeFilename(
    String originalName,
    String extension
  ) {
    if (originalName == null) {
      return "avatar." + extension;
    }

    String name = originalName.replace('\\', '/');
    name = name.substring(name.lastIndexOf('/') + 1);

    String sanitized = name.codePoints()
      .filter(codePoint ->
        !Character.isISOControl(codePoint)
      )
      .limit(255)
      .collect(
        StringBuilder::new,
        StringBuilder::appendCodePoint,
        StringBuilder::append
      )
      .toString()
      .strip();

    return sanitized.isBlank()
      ? "avatar." + extension
      : sanitized;
  }

  private void deleteObjectQuietly(String storageKey) {
    try {
      r2S3Client.deleteObject(
        DeleteObjectRequest.builder()
          .bucket(r2Properties.getBucket())
          .key(storageKey)
          .build()
      );

    } catch (RuntimeException exception) {
      log.error(
        "Failed to clean up R2 object: {}",
        storageKey,
        exception
      );
    }
  }

  private BusinessException invalidFile() {
    return new BusinessException(
      ErrorCode.BAD_REQUEST
    );
  }
}
