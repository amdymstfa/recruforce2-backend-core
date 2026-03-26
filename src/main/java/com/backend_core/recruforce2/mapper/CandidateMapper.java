package com.backend_core.recruforce2.mapper;

import com.backend_core.recruforce2.domain.entities.Candidate;
import com.backend_core.recruforce2.dto.request.CandidateProfileRequest;
import com.backend_core.recruforce2.dto.response.CandidateProfileResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper for Candidate entity ↔ DTOs conversion.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CandidateMapper {

  /**
   * Converts Candidate entity to CandidateProfileResponse DTO.
   */
  @Mapping(target = "applicationsCount", expression = "java(candidate.getApplications() != null ? candidate.getApplications().size() : 0)")
  CandidateProfileResponse toResponse(Candidate candidate);

  /**
   * Converts CandidateProfileRequest DTO to Candidate entity.
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "cvPath", ignore = true)
  @Mapping(target = "parsedCvId", ignore = true)
  @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
  @Mapping(target = "experiences", ignore = true)
  @Mapping(target = "educations", ignore = true)
  @Mapping(target = "skills", ignore = true)
  @Mapping(target = "languages", ignore = true)
  @Mapping(target = "applications", ignore = true)
  Candidate toEntity(CandidateProfileRequest request);
}
