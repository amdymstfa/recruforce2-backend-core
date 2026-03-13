package com.backend_core.recruforce2.mapper;

import com.backend_core.recruforce2.domain.entities.Feedback;
import com.backend_core.recruforce2.dto.response.FeedbackResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for Feedback entity ↔ DTOs conversion.
 */
@Mapper(componentModel = "spring")
public interface FeedbackMapper {

  /**
   * Converts Feedback entity to FeedbackResponse DTO.
   */
  @Mapping(target = "interviewId", source = "interview.id")
  @Mapping(target = "evaluatorId", source = "evaluator.id")
  @Mapping(target = "evaluatorName", expression = "java(feedback.getEvaluator().getFullName())")
  @Mapping(target = "criteriaEvaluations", source = "criteriaEvaluations")
  FeedbackResponse toResponse(Feedback feedback);
}
