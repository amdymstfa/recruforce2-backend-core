package com.backend_core.recruforce2.dto.response;

import com.backend_core.recruforce2.domain.enums.Role;
import lombok.*;

/**
 * Response DTO returned after successful authentication.
 * Contains access token, refresh token, and user info.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

  private String accessToken;
  private String refreshToken;
  private String tokenType;

  private Long userId;
  private String email;
  private String firstName;
  private String lastName;
  private Role role;

  /**
   * Factory method for creating an auth response with "Bearer" token type.
   */
  public static AuthResponse of(String accessToken, String refreshToken, UserResponse user) {
    return AuthResponse.builder()
      .accessToken(accessToken)
      .refreshToken(refreshToken)
      .tokenType("Bearer")
      .userId(user.getId())
      .email(user.getEmail())
      .firstName(user.getFirstName())
      .lastName(user.getLastName())
      .role(user.getRole())
      .build();
  }
}
