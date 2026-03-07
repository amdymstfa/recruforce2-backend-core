package com.backend_core.recruforce2.dto.response;

import com.backend_core.recruforce2.domain.enums.Role;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Response DTO for user information.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

  private Long id;
  private String email;
  private String firstName;
  private String lastName;
  private String phone;
  private Role role;
  private Boolean isActive;
  private LocalDateTime createdAt;
}
