package com.hikari.rent.file.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FileUrlService {

  @Value("${storage.public-base-url:}")
  private String publicBaseUrl;

  public String getPublicUrl(String storageKey) {
    if (storageKey == null || storageKey.isBlank()) {
      return null;
    }

    if (publicBaseUrl == null || publicBaseUrl.isBlank()) {
      return null;
    }

    String baseUrl = publicBaseUrl
      .trim()
      .replaceAll("/+$", "");

    String key = storageKey.replaceAll("^/+", "");

    return baseUrl + "/" + key;
  }
}
