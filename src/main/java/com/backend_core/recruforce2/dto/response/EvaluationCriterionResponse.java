package com.backend_core.recruforce2.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EvaluationCriterionResponse {

  private Long criterionId;
  private String criterionName;
  private Integer score;

}
