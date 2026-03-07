package com.backend_core.recruforce2.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for candidate profile information.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateProfileResponse {

  private Long id;
  private String firstName;
  private String lastName;
  private String email;
  private String phone;
  private String address;
  private LocalDate birthDate;

  private String cvPath;
  private String parsedCvId;

  private List<ExperienceResponse> experiences;
  private List<EducationResponse> educations;
  private List<CandidateSkillResponse> skills;
  private List<LanguageResponse> languages;

  private Integer applicationsCount;

  private LocalDateTime createdAt;
}
