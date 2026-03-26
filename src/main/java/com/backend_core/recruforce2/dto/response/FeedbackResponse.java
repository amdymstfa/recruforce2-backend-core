package com.backend_core.recruforce2.dto.response;

import com.backend_core.recruforce2.dto.response.EvaluationCriterionResponse;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class FeedbackResponse {

  private Long id;

  private Long interviewId;
  private Long evaluatorId;
  private String evaluatorName;

  private Integer overallScore;

  private String generalComment;
  private String recommendation;

  private LocalDateTime createdAt;

  private List<EvaluationCriterionResponse> criteriaEvaluations;
}
