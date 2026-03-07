package com.backend_core.recruforce2.dto.response;

import lombok.*;

import java.util.List;

/**
 * Response DTO for AI matching score between a candidate and a job offer.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchingScoreResponse {

  private Long candidateId;
  private String candidateName;

  private Long jobOfferId;
  private String jobOfferTitle;

  private Integer matchingScore;
  private Boolean isQualified;

  private List<String> matchedSkills;
  private List<String> missingSkills;

  private String modelVersion;
  private Double confidence;
}
