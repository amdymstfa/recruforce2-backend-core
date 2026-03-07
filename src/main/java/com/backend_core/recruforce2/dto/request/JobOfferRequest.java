package com.backend_core.recruforce2.dto.request;

import com.backend_core.recruforce2.domain.enums.ContractType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Request DTO for creating or updating a job offer.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobOfferRequest {

  @NotBlank(message = "Job title is required")
  @Size(max = 200)
  private String title;

  @NotBlank(message = "Job description is required")
  private String description;

  @Size(max = 200)
  private String location;

  @NotNull(message = "Contract type is required")
  private ContractType contractType;

  @Positive
  private Integer minExperience;

  @Positive
  private Integer maxExperience;

  private Double minSalary;
  private Double maxSalary;

  private LocalDate expirationDate;

  /** List of skill IDs required for this position */
  private List<Long> requiredSkillIds;

  /** Whether to publish this offer on LinkedIn */
  private Boolean publishOnLinkedin;
}
