package com.backend_core.recruforce2.service;

import com.backend_core.recruforce2.domain.entities.User;
import com.backend_core.recruforce2.domain.enums.Role;
import com.backend_core.recruforce2.dto.request.LoginRequest;
import com.backend_core.recruforce2.dto.request.RegisterRequest;
import com.backend_core.recruforce2.dto.response.AuthResponse;
import com.backend_core.recruforce2.repository.NotificationPreferencesRepository;
import com.backend_core.recruforce2.repository.UserRepository;
import com.backend_core.recruforce2.util.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private NotificationPreferencesRepository notificationPreferencesRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private JwtTokenProvider jwtTokenProvider;

  @Mock
  private AuthenticationManager authenticationManager;

  @InjectMocks
  private AuthService authService;

  private User testUser;
  private RegisterRequest registerRequest;
  private LoginRequest loginRequest;

  @BeforeEach
  void setUp() {
    testUser = User.builder()
      .id(1L)
      .email("test@example.com")
      .password("encoded_password")
      .firstName("John")
      .lastName("Doe")
      .role(Role.RECRUITER)
      .isActive(true)
      .createdAt(LocalDateTime.now())
      .build();

    registerRequest = RegisterRequest.builder()
      .email("test@example.com")
      .password("password123")
      .firstName("John")
      .lastName("Doe")
      .role(Role.RECRUITER)
      .build();

    loginRequest = LoginRequest.builder()
      .email("test@example.com")
      .password("password123")
      .build();
  }

  @Test
  void register_Success() {
    // Given
    when(userRepository.existsByEmail(anyString())).thenReturn(false);
    when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");
    when(userRepository.save(any(User.class))).thenReturn(testUser);
    when(jwtTokenProvider.generateToken(any(User.class))).thenReturn("access_token");
    when(jwtTokenProvider.generateRefreshToken(any(User.class))).thenReturn("refresh_token");

    // When
    AuthResponse response = authService.register(registerRequest);

    // Then
    assertThat(response).isNotNull();
    assertThat(response.getAccessToken()).isEqualTo("access_token");
    assertThat(response.getRefreshToken()).isEqualTo("refresh_token");
    assertThat(response.getEmail()).isEqualTo("test@example.com");

    verify(userRepository).save(any(User.class));
    verify(notificationPreferencesRepository).save(any());
  }

  @Test
  void register_EmailAlreadyExists_ThrowsException() {
    // Given
    when(userRepository.existsByEmail(anyString())).thenReturn(true);

    // When / Then
    assertThatThrownBy(() -> authService.register(registerRequest))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("Email already registered");

    verify(userRepository, never()).save(any());
  }

  @Test
  void login_Success() {
    // Given
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
    when(jwtTokenProvider.generateToken(any(User.class))).thenReturn("access_token");
    when(jwtTokenProvider.generateRefreshToken(any(User.class))).thenReturn("refresh_token");

    // When
    AuthResponse response = authService.login(loginRequest);

    // Then
    assertThat(response).isNotNull();
    assertThat(response.getAccessToken()).isEqualTo("access_token");
    assertThat(response.getEmail()).isEqualTo("test@example.com");

    verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
  }

  @Test
  void login_InactiveUser_ThrowsException() {
    // Given
    testUser.setIsActive(false);
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));

    // When / Then
    assertThatThrownBy(() -> authService.login(loginRequest))
      .isInstanceOf(IllegalStateException.class)
      .hasMessage("User account is deactivated");
  }

  @Test
  void refreshToken_Success() {
    // Given
    String refreshToken = "valid_refresh_token";
    when(jwtTokenProvider.extractUsername(anyString())).thenReturn("test@example.com");
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
    when(jwtTokenProvider.validateToken(anyString(), any(User.class))).thenReturn(true);
    when(jwtTokenProvider.generateToken(any(User.class))).thenReturn("new_access_token");
    when(jwtTokenProvider.generateRefreshToken(any(User.class))).thenReturn("new_refresh_token");

    // When
    AuthResponse response = authService.refreshToken(refreshToken);

    // Then
    assertThat(response).isNotNull();
    assertThat(response.getAccessToken()).isEqualTo("new_access_token");
    assertThat(response.getRefreshToken()).isEqualTo("new_refresh_token");
  }

  @Test
  void refreshToken_InvalidToken_ThrowsException() {
    // Given
    String refreshToken = "invalid_refresh_token";
    when(jwtTokenProvider.extractUsername(anyString())).thenReturn("test@example.com");
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
    when(jwtTokenProvider.validateToken(anyString(), any(User.class))).thenReturn(false);

    // When / Then
    assertThatThrownBy(() -> authService.refreshToken(refreshToken))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("Invalid refresh token");
  }
}
