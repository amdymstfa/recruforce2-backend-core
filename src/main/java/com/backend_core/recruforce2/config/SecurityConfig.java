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
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * Spring Security configuration for JWT-based authentication.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthFilter;
  private final UserDetailsService userDetailsService;
  private final CorsConfigurationSource corsConfigurationSource;

  public SecurityConfig(
    @Lazy JwtAuthenticationFilter jwtAuthFilter,
    @Lazy UserDetailsService userDetailsService,
    CorsConfigurationSource corsConfigurationSource
  ) {
    this.jwtAuthFilter = jwtAuthFilter;
    this.userDetailsService = userDetailsService;
    this.corsConfigurationSource = corsConfigurationSource;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
      .csrf(AbstractHttpConfigurer::disable)
      .cors(cors -> cors.configurationSource(corsConfigurationSource))
      .authorizeHttpRequests(auth -> auth

        .requestMatchers(
          "/api/auth/**",
          "/api/public/**",
          "/swagger-ui/**",
          "/v3/api-docs/**",
          "/swagger-ui.html",
          "/actuator/**", // Pour le healthcheck Docker
          "/actuator/health/**"
        ).permitAll()

        // 2. Endpoints réservés aux Admins
        .requestMatchers("/api/admin/**").hasRole("ADMIN")
        .requestMatchers("/api/users/**").hasRole("ADMIN")


        .requestMatchers("/api/job-offers/**", "/api/applications/**").hasAnyRole("RECRUITER", "ADMIN")


        .requestMatchers("/api/candidates/**", "/api/interviews/**", "/api/feedbacks/**", "/api/ml-models/**", "/api/predictions/**")
        .hasAnyRole("MANAGER", "RECRUITER", "ADMIN")


        .anyRequest().authenticated()
      )
      .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .authenticationProvider(authenticationProvider())
      .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService);
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12);
  }
}
