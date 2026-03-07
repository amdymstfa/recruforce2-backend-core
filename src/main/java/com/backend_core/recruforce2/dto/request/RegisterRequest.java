package com.backend_core.recruforce2.dto.request;

import com.backend_core.recruforce2.domain.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Request DTO for user registration.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {

  @Email(message = "Email must be valid")
  @NotBlank(message = "Email is required")
  private String email;

  @NotBlank(message = "Password is required")
  @Size(min = 8, message = "Password must be at least 8 characters")
  private String password;

  @NotBlank(message = "First name is required")
  @Size(max = 100)
  private String firstName;

  @NotBlank(message = "Last name is required")
  @Size(max = 100)
  private String lastName;

  @Size(max = 20)
  private String phone;

  @NotNull(message = "Role is required")
  private Role role;
}
