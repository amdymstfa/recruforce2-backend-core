package com.backend_core.recruforce2.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Request DTO for creating or updating a candidate profile.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateProfileRequest {

  @NotBlank(message = "First name is required")
  @Size(max = 100)
  private String firstName;

  @NotBlank(message = "Last name is required")
  @Size(max = 100)
  private String lastName;

  @Email(message = "Email must be valid")
  @NotBlank(message = "Email is required")
  private String email;

  @Size(max = 20)
  private String phone;

  @Size(max = 250)
  private String address;

  private LocalDate birthDate;

  private String parsedCvId;

  private List<String> skills;
  private List<ExperienceRequest> experiences;
}
