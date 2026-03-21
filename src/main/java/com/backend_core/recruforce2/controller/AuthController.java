package com.backend_core.recruforce2.controller;

import com.backend_core.recruforce2.dto.request.LoginRequest;
import com.backend_core.recruforce2.dto.request.RefreshTokenRequest;
import com.backend_core.recruforce2.dto.request.RegisterRequest;
import com.backend_core.recruforce2.dto.response.AuthResponse;
import com.backend_core.recruforce2.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for authentication operations.
 * Public endpoints - no authentication required.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User authentication and registration endpoints")
public class AuthController {

  private final AuthService authService;

  @Operation(summary = "Register a new user")
  @PostMapping("/register")
  public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
  }

  @Operation(summary = "Login with email and password")
  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
    return ResponseEntity.ok(authService.login(request));
  }

  @Operation(summary = "Refresh access token using refresh token")
  @PostMapping("/refresh")
  public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
    return ResponseEntity.ok(authService.refreshToken(request.getRefreshToken()));
  }

  @Operation(summary = "Generate service token for internal integrations (90 days)")
  @PostMapping("/service-token")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<AuthResponse> generateServiceToken(Authentication authentication) {
    return ResponseEntity.ok(authService.generateServiceToken(authentication.getName()));
  }

}
