package com.backend_core.recruforce2.mapper;

import com.backend_core.recruforce2.domain.entities.Prediction;
import com.backend_core.recruforce2.dto.response.PredictionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for Prediction entity ↔ DTOs conversion.
 */
@Mapper(componentModel = "spring")
public interface PredictionMapper {

  /**
   * Converts Prediction entity to PredictionResponse DTO.
   */
  @Mapping(target = "candidateId", source = "candidate.id")
  @Mapping(target = "candidateName", expression = "java(prediction.getCandidate().getFullName())")
  @Mapping(target = "jobOfferId", source = "jobOffer.id")
  @Mapping(target = "jobOfferTitle", source = "jobOffer.title")
  @Mapping(target = "modelId", source = "model.id")
  PredictionResponse toResponse(Prediction prediction);
}
