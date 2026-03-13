package com.backend_core.recruforce2.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response DTO for criterion evaluation details.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CriterionEvaluationResponse {
  private Long id;
  private String criterionName;
  private Integer score;
  private String comment;
}
