package com.backend_core.recruforce2.mapper;

import com.backend_core.recruforce2.domain.entities.Criterion;
import com.backend_core.recruforce2.dto.request.CriteriaRequest;
import com.backend_core.recruforce2.dto.response.CriteriaResponse;

public interface CriteriaMapper {

  public static Criterion toEntity(CriteriaRequest request) {
    return Criterion.builder()
      .name(request.getName())
      .description(request.getDescription())
      .interviewType(request.getInterviewType())
      .weight(request.getWeight() != null ? request.getWeight() : 1)
      .isActive(request.getIsActive() != null ? request.getIsActive() : true)
      .build();
  }

  public static CriteriaResponse toResponse(Criterion entity) {
    return CriteriaResponse.builder()
      .id(entity.getId())
      .name(entity.getName())
      .description(entity.getDescription())
      .interviewType(entity.getInterviewType())
      .weight(entity.getWeight())
      .isActive(entity.getIsActive())
      .build();
  }

  public static void updateEntity(Criterion entity, CriteriaRequest request) {
    entity.setName(request.getName());
    entity.setDescription(request.getDescription());
    entity.setInterviewType(request.getInterviewType());
    entity.setWeight(request.getWeight());
    entity.setIsActive(request.getIsActive());
  }
}
