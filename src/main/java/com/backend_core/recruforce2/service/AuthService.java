package com.backend_core.recruforce2.service;

import com.backend_core.recruforce2.domain.entities.User;
import com.backend_core.recruforce2.domain.entities.NotificationPreferences;
import com.backend_core.recruforce2.dto.request.LoginRequest;
import com.backend_core.recruforce2.dto.request.RegisterRequest;
import com.backend_core.recruforce2.dto.response.AuthResponse;
import com.backend_core.recruforce2.dto.response.UserResponse;
import com.backend_core.recruforce2.mapper.UserMapper;
import com.backend_core.recruforce2.repository.UserRepository;
import com.backend_core.recruforce2.repository.NotificationPreferencesRepository;
import com.backend_core.recruforce2.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Service handling authentication operations and implementing Spring Security's UserDetailsService.
 * <p>
 * Responsibilities:
 * - User registration
 * - User login (JWT generation)
 * - Token refresh
 * - Loading user details for Spring Security
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService implements UserDetailsService {

  private final UserRepository userRepository;
  private final NotificationPreferencesRepository notificationPreferencesRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider jwtTokenProvider;
  private final AuthenticationManager authenticationManager;
  private final UserMapper userMapper;

  @Transactional
  public AuthResponse register(RegisterRequest request) {
    log.info("Registering new user with email: {}", request.getEmail());

    if (userRepository.existsByEmail(request.getEmail())) {
      throw new IllegalArgumentException("Email already registered");
    }

    // Use mapper to convert DTO → Entity
    User user = userMapper.toEntity(request);
    user.setPassword(passwordEncoder.encode(request.getPassword()));

    user = userRepository.save(user);
    log.info("User registered successfully with ID: {}", user.getId());

    // Create default notification preferences
    NotificationPreferences preferences = NotificationPreferences.builder()
      .user(user)
      .emailNewCandidate(true)
      .emailInterview(true)
      .emailDeadline(true)
      .inAppNotifications(true)
      .build();
    notificationPreferencesRepository.save(preferences);

    // Generate tokens
    String accessToken = jwtTokenProvider.generateToken(user);
    String refreshToken = jwtTokenProvider.generateRefreshToken(user);

    // Use mapper to convert Entity → DTO
    return AuthResponse.of(accessToken, refreshToken, userMapper.toResponse(user));
  }

  @Transactional(readOnly = true)
  public AuthResponse login(LoginRequest request) {
    log.info("Login attempt for email: {}", request.getEmail());

    authenticationManager.authenticate(
      new UsernamePasswordAuthenticationToken(
        request.getEmail(),
        request.getPassword()
      )
    );

    User user = userRepository.findByEmail(request.getEmail())
      .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    if (!user.getIsActive()) {
      throw new IllegalStateException("User account is deactivated");
    }

    String accessToken = jwtTokenProvider.generateToken(user);
    String refreshToken = jwtTokenProvider.generateRefreshToken(user);

    log.info("User logged in successfully: {}", user.getEmail());

    // Use mapper instead of manual mapping
    return AuthResponse.of(accessToken, refreshToken, userMapper.toResponse(user));
  }

  @Transactional(readOnly = true)
  public AuthResponse refreshToken(String refreshToken) {
    log.info("Refreshing access token");

    String userEmail = jwtTokenProvider.extractUsername(refreshToken);

    User user = userRepository.findByEmail(userEmail)
      .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    if (!jwtTokenProvider.validateToken(refreshToken, user)) {
      throw new IllegalArgumentException("Invalid refresh token");
    }

    String newAccessToken = jwtTokenProvider.generateToken(user);
    String newRefreshToken = jwtTokenProvider.generateRefreshToken(user);

    log.info("Tokens refreshed successfully for user: {}", user.getEmail());

    // Use mapper
    return AuthResponse.of(newAccessToken, newRefreshToken, userMapper.toResponse(user));
  }

  @Override
  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return userRepository.findByEmail(username)
      .orElseThrow(() -> new UsernameNotFoundException(
        "User not found with email: " + username));
  }
}
