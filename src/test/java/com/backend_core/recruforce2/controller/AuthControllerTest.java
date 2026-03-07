package com.backend_core.recruforce2.controller;

import com.backend_core.recruforce2.dto.request.LoginRequest;
import com.backend_core.recruforce2.dto.request.RegisterRequest;
import com.backend_core.recruforce2.domain.enums.Role;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void register_Success() throws Exception {
    // Given
    RegisterRequest request = RegisterRequest.builder()
      .email("newuser@example.com")
      .password("password123")
      .firstName("New")
      .lastName("User")
      .role(Role.RECRUITER)
      .build();

    // When / Then
    mockMvc.perform(post("/api/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.accessToken").exists())
      .andExpect(jsonPath("$.refreshToken").exists())
      .andExpect(jsonPath("$.email").value("newuser@example.com"))
      .andExpect(jsonPath("$.role").value("RECRUITER"));
  }

  @Test
  void register_InvalidEmail_ReturnsBadRequest() throws Exception {
    // Given
    RegisterRequest request = RegisterRequest.builder()
      .email("invalid-email")
      .password("password123")
      .firstName("New")
      .lastName("User")
      .role(Role.RECRUITER)
      .build();

    // When / Then
    mockMvc.perform(post("/api/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.error").value("Validation Failed"));
  }

  @Test
  void register_DuplicateEmail_ReturnsBadRequest() throws Exception {
    // Given - First registration
    RegisterRequest firstRequest = RegisterRequest.builder()
      .email("duplicate@example.com")
      .password("password123")
      .firstName("First")
      .lastName("User")
      .role(Role.RECRUITER)
      .build();

    mockMvc.perform(post("/api/auth/register")
      .contentType(MediaType.APPLICATION_JSON)
      .content(objectMapper.writeValueAsString(firstRequest)));

    // When - Second registration with same email
    RegisterRequest secondRequest = RegisterRequest.builder()
      .email("duplicate@example.com")
      .password("password456")
      .firstName("Second")
      .lastName("User")
      .role(Role.ADMIN)
      .build();

    // Then
    mockMvc.perform(post("/api/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(secondRequest)))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.message").value("Email already registered"));
  }

  @Test
  void login_Success() throws Exception {
    // Given - Register user first
    RegisterRequest registerRequest = RegisterRequest.builder()
      .email("logintest@example.com")
      .password("password123")
      .firstName("Login")
      .lastName("Test")
      .role(Role.RECRUITER)
      .build();

    mockMvc.perform(post("/api/auth/register")
      .contentType(MediaType.APPLICATION_JSON)
      .content(objectMapper.writeValueAsString(registerRequest)));

    // When - Login
    LoginRequest loginRequest = LoginRequest.builder()
      .email("logintest@example.com")
      .password("password123")
      .build();

    // Then
    mockMvc.perform(post("/api/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(loginRequest)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.accessToken").exists())
      .andExpect(jsonPath("$.email").value("logintest@example.com"));
  }

  @Test
  void login_InvalidCredentials_ReturnsUnauthorized() throws Exception {
    // Given
    LoginRequest request = LoginRequest.builder()
      .email("nonexistent@example.com")
      .password("wrongpassword")
      .build();

    // When / Then
    mockMvc.perform(post("/api/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isUnauthorized());
  }

  @Test
  void login_MissingPassword_ReturnsBadRequest() throws Exception {
    // Given
    LoginRequest request = LoginRequest.builder()
      .email("test@example.com")
      .build();

    // When / Then
    mockMvc.perform(post("/api/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.error").value("Validation Failed"));
  }
}
