package com.backend_core.recruforce2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security configuration for JWT-based authentication.
 * <p>
 * Configures:
 * - Stateless session management (JWT-based)
 * - Public and protected endpoints
 * - JWT authentication filter
 * - Password encoding (BCrypt)
 * - CORS integration
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthFilter;
  private final UserDetailsService userDetailsService;

  public SecurityConfig(
    @Lazy JwtAuthenticationFilter jwtAuthFilter,
    @Lazy UserDetailsService userDetailsService
  ) {
    this.jwtAuthFilter = jwtAuthFilter;
    this.userDetailsService = userDetailsService;
  }

  /**
   * Configures the security filter chain.
   * Defines which endpoints are public and which require authentication.
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
      .csrf(AbstractHttpConfigurer::disable)
      .cors(cors -> cors.configure(http))
      .authorizeHttpRequests(auth -> auth
        // Public endpoints
        .requestMatchers(
          "/api/auth/**",
          "/api/public/**",
          "/swagger-ui/**",
          "/v3/api-docs/**",
          "/swagger-ui.html",
          "/actuator/health",
          "/actuator/info"
        ).permitAll()

        // Admin-only endpoints
        .requestMatchers("/api/admin/**").hasRole("ADMIN")

        // Recruiter and Admin endpoints
        .requestMatchers("/api/job-offers/**", "/api/applications/**")
        .hasAnyRole("RECRUITER", "ADMIN")

        // Manager, Recruiter, and Admin endpoints
        .requestMatchers("/api/interviews/**", "/api/feedbacks/**")
        .hasAnyRole("MANAGER", "RECRUITER", "ADMIN")

        // All other requests require authentication
        .anyRequest().authenticated()
      )
      .sessionManagement(session -> session
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      )
      .authenticationProvider(authenticationProvider())
      .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  /**
   * Configures the authentication provider with UserDetailsService and password encoder.
   */
  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService);
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
  }

  /**
   * Provides the authentication manager bean.
   */
  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
  }

  /**
   * BCrypt password encoder with strength 12.
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12);
  }
}
