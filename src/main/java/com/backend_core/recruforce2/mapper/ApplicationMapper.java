package com.backend_core.recruforce2.mapper;

import com.backend_core.recruforce2.domain.entities.Application;
import com.backend_core.recruforce2.dto.response.ApplicationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for Application entity ↔ DTOs conversion.
 */
@Mapper(componentModel = "spring")
public interface ApplicationMapper {

  /**
   * Converts Application entity to ApplicationResponse DTO.
   */
  @Mapping(target = "candidateId", source = "candidate.id")
  @Mapping(target = "candidateName", expression = "java(application.getCandidate().getFullName())")
  @Mapping(target = "candidateEmail", source = "candidate.email")
  @Mapping(target = "jobOfferId", source = "jobOffer.id")
  @Mapping(target = "jobOfferTitle", source = "jobOffer.title")
  ApplicationResponse toResponse(Application application);
}
