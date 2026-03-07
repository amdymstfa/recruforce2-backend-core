package com.backend_core.recruforce2.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Request DTO for refreshing an access token using a refresh token.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshTokenRequest {

  @NotBlank(message = "Refresh token is required")
  private String refreshToken;
}
