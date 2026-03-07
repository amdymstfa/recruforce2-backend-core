package com.backend_core.recruforce2.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Request DTO for creating an application.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationRequest {

  @NotNull(message = "Candidate ID is required")
  private Long candidateId;

  @NotNull(message = "Job offer ID is required")
  private Long jobOfferId;

  private String coverLetter;

  private String sourceChannel;
}
