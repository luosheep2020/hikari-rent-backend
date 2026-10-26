package com.hikari.rent.common.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class ElasticsearchConfig {

  @Value("${app.elasticsearch.uri}")
  private String uri;

  @Value("${app.elasticsearch.username:}")
  private String username;

  @Value("${app.elasticsearch.password:}")
  private String password;

  @Bean(destroyMethod = "close")
  public RestClientTransport rentalElasticsearchTransport() {
    RestClientBuilder builder =
      RestClient.builder(
        HttpHost.create(uri)
      );

    builder.setRequestConfigCallback(config ->
      config
        .setConnectTimeout(5_000)
        .setSocketTimeout(15_000)
        .setConnectionRequestTimeout(5_000)
    );

    if (!username.isBlank()) {
      BasicCredentialsProvider credentials =
        new BasicCredentialsProvider();

      credentials.setCredentials(
        AuthScope.ANY,
        new UsernamePasswordCredentials(
          username,
          password
        )
      );

      builder.setHttpClientConfigCallback(client ->
        client.setDefaultCredentialsProvider(
          credentials
        )
      );
    }

    RestClient restClient = builder.build();

    // Keep the Elasticsearch mapper separate from Spring MVC.
    return new RestClientTransport(
      restClient,
      new JacksonJsonpMapper()
    );
  }

  @Bean
  public ElasticsearchClient rentalElasticsearchClient(
    RestClientTransport rentalElasticsearchTransport
  ) {
    return new ElasticsearchClient(
      rentalElasticsearchTransport
    );
  }
}
