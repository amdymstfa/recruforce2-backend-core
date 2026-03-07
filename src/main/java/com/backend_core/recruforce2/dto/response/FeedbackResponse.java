package com.backend_core.recruforce2.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for interview feedback.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedbackResponse {

  private Long id;
  private Long interviewId;

  private Long evaluatorId;
  private String evaluatorName;

  private Integer overallScore;
  private String generalComment;
  private String recommendation;

  private List<CriterionEvaluationResponse> criteriaEvaluations;

  private LocalDateTime createdAt;

  /**
   * Nested DTO for criterion evaluation details.
   */
  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class CriterionEvaluationResponse {
    private Long id;
    private String criterionName;
    private Integer score;
    private String comment;
  }
}
