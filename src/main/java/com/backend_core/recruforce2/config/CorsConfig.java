package com.backend_core.recruforce2.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * CORS (Cross-Origin Resource Sharing) configuration.
 * <p>
 * Allows the Angular frontend to communicate with the backend API.
 * Configuration is profile-specific (dev vs prod).
 */
@Configuration
public class CorsConfig {

  @Value("${cors.allowed-origins}")
  private String allowedOrigins;

  @Value("${cors.allowed-methods}")
  private String allowedMethods;

  @Value("${cors.allowed-headers}")
  private String allowedHeaders;

  @Value("${cors.allow-credentials}")
  private boolean allowCredentials;

  /**
   * Configures CORS settings based on application properties.
   */
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();

    // Parse comma-separated values from properties
    configuration.setAllowedOrigins(parseList(allowedOrigins));
    configuration.setAllowedMethods(parseList(allowedMethods));
    configuration.setAllowedHeaders(parseList(allowedHeaders));
    configuration.setAllowCredentials(allowCredentials);

    // Allow Authorization header for JWT
    configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));

    // Apply CORS configuration to all endpoints
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);

    return source;
  }

  /**
   * Parses a comma-separated string into a list.
   */
  private List<String> parseList(String value) {
    return Arrays.stream(value.split(","))
      .map(String::trim)
      .toList();
  }
}
