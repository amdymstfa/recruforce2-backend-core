package com.backend_core.recruforce2.dto.response;

import com.backend_core.recruforce2.domain.enums.ApplicationStatus;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Response DTO for application information.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationResponse {

  private Long id;
  private Long candidateId;
  private String candidateName;
  private String candidateEmail;

  private Long jobOfferId;
  private String jobOfferTitle;

  private LocalDateTime receivedAt;
  private ApplicationStatus status;

  private Integer matchingScore;
  private Boolean isQualified;

  private String cvFilePath;
  private String coverLetter;
  private String sourceChannel;
}
