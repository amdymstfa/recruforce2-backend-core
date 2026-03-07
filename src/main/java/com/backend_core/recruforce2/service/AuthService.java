package com.backend_core.recruforce2.service;

import com.backend_core.recruforce2.domain.entities.User;
import com.backend_core.recruforce2.domain.entities.NotificationPreferences;
import com.backend_core.recruforce2.dto.request.LoginRequest;
import com.backend_core.recruforce2.dto.request.RegisterRequest;
import com.backend_core.recruforce2.dto.response.AuthResponse;
import com.backend_core.recruforce2.dto.response.UserResponse;
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

  /**
   * Registers a new user in the system.
   * Creates default notification preferences for the user.
   *
   * @param request registration details
   * @return authentication response with JWT tokens
   * @throws IllegalArgumentException if email already exists
   */
  @Transactional
  public AuthResponse register(RegisterRequest request) {
    log.info("Registering new user with email: {}", request.getEmail());

    // Check if email already exists
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new IllegalArgumentException("Email already registered");
    }

    // Create user entity
    User user = User.builder()
      .email(request.getEmail())
      .password(passwordEncoder.encode(request.getPassword()))
      .firstName(request.getFirstName())
      .lastName(request.getLastName())
      .phone(request.getPhone())
      .role(request.getRole())
      .isActive(true)
      .createdAt(LocalDateTime.now())
      .updatedAt(LocalDateTime.now())
      .build();

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

    return AuthResponse.of(accessToken, refreshToken, mapToUserResponse(user));
  }

  /**
   * Authenticates a user and generates JWT tokens.
   *
   * @param request login credentials
   * @return authentication response with JWT tokens
   * @throws UsernameNotFoundException if user not found
   */
  @Transactional(readOnly = true)
  public AuthResponse login(LoginRequest request) {
    log.info("Login attempt for email: {}", request.getEmail());

    // Authenticate user
    authenticationManager.authenticate(
      new UsernamePasswordAuthenticationToken(
        request.getEmail(),
        request.getPassword()
      )
    );

    // Load user details
    User user = userRepository.findByEmail(request.getEmail())
      .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    // Check if user is active
    if (!user.getIsActive()) {
      throw new IllegalStateException("User account is deactivated");
    }

    // Generate tokens
    String accessToken = jwtTokenProvider.generateToken(user);
    String refreshToken = jwtTokenProvider.generateRefreshToken(user);

    log.info("User logged in successfully: {}", user.getEmail());

    return AuthResponse.of(accessToken, refreshToken, mapToUserResponse(user));
  }

  /**
   * Refreshes an access token using a valid refresh token.
   *
   * @param refreshToken the refresh token
   * @return new authentication response with fresh tokens
   * @throws IllegalArgumentException if refresh token is invalid
   */
  @Transactional(readOnly = true)
  public AuthResponse refreshToken(String refreshToken) {
    log.info("Refreshing access token");

    // Extract username from refresh token
    String userEmail = jwtTokenProvider.extractUsername(refreshToken);

    // Load user
    User user = userRepository.findByEmail(userEmail)
      .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    // Validate refresh token
    if (!jwtTokenProvider.validateToken(refreshToken, user)) {
      throw new IllegalArgumentException("Invalid refresh token");
    }

    // Generate new tokens
    String newAccessToken = jwtTokenProvider.generateToken(user);
    String newRefreshToken = jwtTokenProvider.generateRefreshToken(user);

    log.info("Tokens refreshed successfully for user: {}", user.getEmail());

    return AuthResponse.of(newAccessToken, newRefreshToken, mapToUserResponse(user));
  }

  /**
   * Loads user details by username (email) for Spring Security.
   * Required by UserDetailsService interface.
   *
   * @param username the email address
   * @return user details
   * @throws UsernameNotFoundException if user not found
   */
  @Override
  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return userRepository.findByEmail(username)
      .orElseThrow(() -> new UsernameNotFoundException(
        "User not found with email: " + username));
  }

  /**
   * Maps a User entity to UserResponse DTO.
   */
  private UserResponse mapToUserResponse(User user) {
    return UserResponse.builder()
      .id(user.getId())
      .email(user.getEmail())
      .firstName(user.getFirstName())
      .lastName(user.getLastName())
      .phone(user.getPhone())
      .role(user.getRole())
      .isActive(user.getIsActive())
      .createdAt(user.getCreatedAt())
      .build();
  }
}
