package com.backend_core.recruforce2.dto.response;

import com.backend_core.recruforce2.domain.enums.ContractType;
import com.backend_core.recruforce2.domain.enums.OfferStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for job offer information.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobOfferResponse {

  private Long id;
  private String title;
  private String description;
  private String location;
  private ContractType contractType;
  private OfferStatus status;

  private Integer minExperience;
  private Integer maxExperience;
  private Double minSalary;
  private Double maxSalary;

  private LocalDate publicationDate;
  private LocalDate expirationDate;

  private Long createdById;
  private String createdByName;

  private List<SkillResponse> requiredSkills;

  private Boolean publishedOnLinkedin;
  private String linkedinJobId;

  private Integer applicationsCount;

  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
