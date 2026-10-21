package com.hikari.rent.file.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.net.URI;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "storage.r2")
public class R2Properties {

  @NotNull
  private URI endpoint;

  @NotBlank
  private String accessKeyId;

  @NotBlank
  private String secretAccessKey;

  @NotBlank
  private String bucket;
}
