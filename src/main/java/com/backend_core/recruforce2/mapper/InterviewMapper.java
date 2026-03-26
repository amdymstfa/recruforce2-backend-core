package com.backend_core.recruforce2.mapper;

import com.backend_core.recruforce2.domain.entities.Interview;
import com.backend_core.recruforce2.dto.response.InterviewResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper for Interview entity ↔ DTOs conversion.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InterviewMapper {

  /**
   * Converts Interview entity to InterviewResponse DTO.
   */
  @Mapping(target = "applicationId", source = "application.id")
  @Mapping(target = "candidateName", expression = "java(interview.getApplication().getCandidate().getFullName())")
  @Mapping(target = "interviewerId", source = "interviewer.id")
  @Mapping(target = "interviewerName", expression = "java(interview.getInterviewer().getFullName())")
  @Mapping(target = "hasFeedback", expression = "java(interview.getFeedback() != null)")
  InterviewResponse toResponse(Interview interview);
}
