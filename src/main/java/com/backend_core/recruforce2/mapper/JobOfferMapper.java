package com.backend_core.recruforce2.mapper;

import com.backend_core.recruforce2.domain.entities.JobOffer;
import com.backend_core.recruforce2.dto.response.JobOfferResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for JobOffer entity ↔ DTOs conversion.
 */
@Mapper(componentModel = "spring", uses = {SkillMapper.class})
public interface JobOfferMapper {

  /**
   * Converts JobOffer entity to JobOfferResponse DTO.
   */
  @Mapping(target = "createdById", source = "createdBy.id")
  @Mapping(target = "createdByName", expression = "java(jobOffer.getCreatedBy().getFullName())")
  @Mapping(target = "applicationsCount", expression = "java(jobOffer.getStatistics())")
  JobOfferResponse toResponse(JobOffer jobOffer);
}
