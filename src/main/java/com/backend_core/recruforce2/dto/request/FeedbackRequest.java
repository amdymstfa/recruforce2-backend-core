package com.backend_core.recruforce2.dto.request;

import com.backend_core.recruforce2.domain.entities.EvaluationCriterion;
import lombok.*;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackRequest {

  @NotNull
  private Long interviewId;

  @NotNull
  private Long evaluatorId;

  private String generalComment;
  private String recommendation;

  private List<EvaluationCriterion> criteria;


}
