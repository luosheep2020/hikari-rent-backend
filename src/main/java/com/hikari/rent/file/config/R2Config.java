package com.hikari.rent.file.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.checksums.RequestChecksumCalculation;
import software.amazon.awssdk.core.checksums.ResponseChecksumValidation;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.time.Duration;

@Configuration
@EnableConfigurationProperties(R2Properties.class)
public class R2Config {

  @Bean(destroyMethod = "close")
  public S3Client r2S3Client(R2Properties properties) {
    AwsBasicCredentials credentials =
      AwsBasicCredentials.create(
        properties.getAccessKeyId(),
        properties.getSecretAccessKey()
      );

    return S3Client.builder()
      .endpointOverride(properties.getEndpoint())
      .region(Region.of("auto"))
      .credentialsProvider(
        StaticCredentialsProvider.create(credentials)
      )
      .serviceConfiguration(
        S3Configuration.builder()
          .pathStyleAccessEnabled(true)
          .chunkedEncodingEnabled(false)
          .build()
      )
      .requestChecksumCalculation(
        RequestChecksumCalculation.WHEN_REQUIRED
      )
      .responseChecksumValidation(
        ResponseChecksumValidation.WHEN_REQUIRED
      )
      .overrideConfiguration(configuration ->
        configuration
          .apiCallTimeout(Duration.ofSeconds(30))
          .apiCallAttemptTimeout(Duration.ofSeconds(15))
      )
      .build();
  }
}
