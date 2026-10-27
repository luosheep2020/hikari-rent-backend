package com.hikari.rent.auth.config;

import com.hikari.rent.auth.security.RedisJwtAuthenticationConverter;
import com.hikari.rent.common.result.ApiResponse;
import jakarta.annotation.Resource;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

  @Resource
  private RedisJwtAuthenticationConverter jwtAuthenticationConverter;

  private final JsonMapper jsonMapper =
    JsonMapper.builder().build();

  @Bean
  public SecurityFilterChain securityFilterChain(
    HttpSecurity http
  ) throws Exception {

    http
      // Authentication uses bearer tokens instead of session cookies.
      .csrf(AbstractHttpConfigurer::disable)

      .cors(cors ->
        cors.configurationSource(corsConfigurationSource())
      )

      // Disable default browser authentication mechanisms.
      .formLogin(AbstractHttpConfigurer::disable)
      .httpBasic(AbstractHttpConfigurer::disable)
      .logout(AbstractHttpConfigurer::disable)
      .requestCache(AbstractHttpConfigurer::disable)

      // Do not create an HTTP session for authentication.
      .sessionManagement(session ->
        session.sessionCreationPolicy(
          SessionCreationPolicy.STATELESS
        )
      )

      .exceptionHandling(exception ->
        exception
          .authenticationEntryPoint(
            this::handleAuthenticationFailure
          )
          .accessDeniedHandler(
            (request, response, ex) ->
              writeError(
                response,
                HttpServletResponse.SC_FORBIDDEN,
                "Access denied"
              )
          )
      )

      .authorizeHttpRequests(authorize ->
        authorize
          // Preserve the original error during error dispatch.
          .dispatcherTypeMatchers(DispatcherType.ERROR)
          .permitAll()

          // Allow CORS preflight requests.
          .requestMatchers(HttpMethod.OPTIONS, "/**")
          .permitAll()

          // Public authentication endpoints.
          .requestMatchers(
            HttpMethod.POST,
            "/api/auth/login",
            "/api/auth/register"
          )
          .permitAll()

          // Public rental and reference-data queries.
          .requestMatchers(
            HttpMethod.GET,
            "/api/rentals",
            "/api/rentals/{id:[0-9]+}",
            "/api/rentals/features",
            "/api/rentals/feature-options",
            "/api/rentals/map",
            "/api/areas/**",
            "/api/stations/**",
            "/api/map/**"
          )
          .permitAll()

          // Review rules must precede general rental-management rules.
          .requestMatchers(
            "/api/admin/rentals/review/*",
            "/api/admin/rentals/*/approve",
            "/api/admin/rentals/*/reject"
          )
          .hasRole("PLATFORM_ADMIN")

          // Authorized managers may submit rentals for review.
          .requestMatchers(
            HttpMethod.POST,
            "/api/admin/rentals/*/submit-review"
          )
          .hasAnyRole(
            "PLATFORM_ADMIN",
            "COMPANY_ADMIN",
            "AGENT"
          )

          // Rental ownership is additionally enforced by the service and SQL.
          .requestMatchers(
            "/api/admin/rentals",
            "/api/admin/rentals/**"
          )
          .hasAnyRole(
            "PLATFORM_ADMIN",
            "COMPANY_ADMIN",
            "AGENT"
          )
// Company administrators may manage their own organization and agents.
          .requestMatchers(
            "/api/admin/departments",
            "/api/admin/departments/**",
            "/api/admin/positions",
            "/api/admin/positions/**",
            "/api/admin/employees",
            "/api/admin/employees/**"
          )
          .hasAnyRole(
            "PLATFORM_ADMIN",
            "COMPANY_ADMIN"
          )
          // Dashboard statistics are restricted by the current user's data scope.
          .requestMatchers(
            HttpMethod.GET,
            "/api/admin/dashboard/overview"
          )
          .hasAnyRole(
            "PLATFORM_ADMIN",
            "COMPANY_ADMIN",
            "AGENT"
          )
          // Other administrative endpoints require platform access.
          .requestMatchers("/api/admin/**")
          .hasRole("PLATFORM_ADMIN")

          // User endpoints require an authenticated account.
          .requestMatchers(
            "/api/auth/me",
            "/api/auth/logout",
            "/api/user/**",
            "/api/messages/**"
          )
          .authenticated()

          // File-purpose and ownership checks remain in the service.
          .requestMatchers(
            HttpMethod.POST,
            "/api/files/upload"
          )
          .authenticated()

          // Require explicit rules for other endpoints.
          .anyRequest()
          .denyAll()
      )

      .oauth2ResourceServer(resource ->
        resource
          .jwt(jwt ->
            jwt.jwtAuthenticationConverter(
              jwtAuthenticationConverter
            )
          )
          .authenticationEntryPoint(
            this::handleAuthenticationFailure
          )
          .accessDeniedHandler(
            (request, response, ex) ->
              writeError(
                response,
                HttpServletResponse.SC_FORBIDDEN,
                "Access denied"
              )
          )
      );

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public UrlBasedCorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();

    configuration.setAllowedOrigins(
      List.of(
        "http://localhost:3000",
        "http://localhost:5173"
      )
    );

    configuration.setAllowedMethods(
      List.of(
        "GET",
        "POST",
        "PUT",
        "PATCH",
        "DELETE",
        "OPTIONS"
      )
    );

    configuration.setAllowedHeaders(
      List.of(
        "Authorization",
        "Content-Type",
        "Accept"
      )
    );

    configuration.setAllowCredentials(false);
    configuration.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source =
      new UrlBasedCorsConfigurationSource();

    source.registerCorsConfiguration(
      "/**",
      configuration
    );

    return source;
  }

  private void handleAuthenticationFailure(
    HttpServletRequest request,
    HttpServletResponse response,
    AuthenticationException exception
  ) throws IOException {

    if (
      exception instanceof OAuth2AuthenticationException oauth2
        && "temporarily_unavailable".equals(
        oauth2.getError().getErrorCode()
      )
    ) {
      writeError(
        response,
        HttpServletResponse.SC_SERVICE_UNAVAILABLE,
        "Authentication service is temporarily unavailable"
      );

      return;
    }

    response.setHeader(
      "WWW-Authenticate",
      "Bearer"
    );

    writeError(
      response,
      HttpServletResponse.SC_UNAUTHORIZED,
      "Authentication required or token is invalid"
    );
  }

  private void writeError(
    HttpServletResponse response,
    int status,
    String message
  ) throws IOException {

    response.setStatus(status);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding(
      StandardCharsets.UTF_8.name()
    );
    response.setHeader("Cache-Control", "no-store");

    jsonMapper.writeValue(
      response.getOutputStream(),
      ApiResponse.failure(status, message)
    );
  }
}
