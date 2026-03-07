package com.backend_core.recruforce2.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

/**
 * Request DTO for submitting interview feedback.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedbackRequest {

  @NotNull(message = "Interview ID is required")
  private Long interviewId;

  private String generalComment;

  private String recommendation;

  /** List of criterion evaluations */
  private List<CriterionEvaluationRequest> criteriaEvaluations;

  /**
   * Nested DTO for a single criterion evaluation.
   */
  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class CriterionEvaluationRequest {

    @NotNull(message = "Criterion ID is required")
    private Long criterionId;

    @NotNull(message = "Score is required")
    @Min(0)
    @Max(10)
    private Integer score;

    private String comment;
  }
}
