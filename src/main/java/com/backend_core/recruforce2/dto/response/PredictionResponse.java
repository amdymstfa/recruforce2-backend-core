package com.backend_core.recruforce2.dto.response;

import lombok.*;

import java.time.LocalDateTime;

/**
 * Response DTO for AI prediction result.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PredictionResponse {

  private Long id;

  private Long candidateId;
  private String candidateName;

  private Long jobOfferId;
  private String jobOfferTitle;

  private Double matchingScore;
  private Double successProbability;
  private Double confidence;

  private String mainFactors;
  private String recommendation;

  private LocalDateTime calculatedAt;

  private String modelVersion;
  private Long modelId;
}
