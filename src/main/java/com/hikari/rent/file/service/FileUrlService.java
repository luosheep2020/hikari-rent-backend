package com.hikari.rent.file.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class FileUrlService {

  private final String publicBaseUrl;

  public FileUrlService(
    @Value("${storage.public-base-url:}") String publicBaseUrl
  ) {
    this.publicBaseUrl = publicBaseUrl.trim();
  }

  public String getPublicUrl(String storageKey) {
    if (
      publicBaseUrl.isBlank()
        || storageKey == null
        || storageKey.isBlank()
    ) {
      return null;
    }

    return UriComponentsBuilder
      .fromUriString(publicBaseUrl)
      .pathSegment(storageKey.split("/"))
      .build()
      .encode()
      .toUriString();
  }
}
