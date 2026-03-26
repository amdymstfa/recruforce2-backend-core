package com.backend_core.recruforce2.mapper;

import com.backend_core.recruforce2.domain.entities.EvaluationCriterion;
import com.backend_core.recruforce2.domain.entities.Feedback;
import com.backend_core.recruforce2.dto.response.EvaluationCriterionResponse;
import com.backend_core.recruforce2.dto.response.FeedbackResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FeedbackMapper {

  // -----------------------------
  // FEEDBACK → RESPONSE
  // -----------------------------
  @Mapping(target = "interviewId", source = "interview.id")
  @Mapping(target = "evaluatorId", source = "evaluator.id")
  @Mapping(target = "evaluatorName",
    expression = "java(feedback.getEvaluator() != null ? feedback.getEvaluator().getFullName() : null)")
  @Mapping(target = "criteriaEvaluations", source = "criteriaEvaluations")
  FeedbackResponse toResponse(Feedback feedback);

  // -----------------------------
  // CRITERION → RESPONSE
  // -----------------------------
  @Mapping(target = "criterionId", source = "criterion.id")
  @Mapping(target = "criterionName", source = "criterion.name")
  EvaluationCriterionResponse toCriterionResponse(EvaluationCriterion entity);
}
