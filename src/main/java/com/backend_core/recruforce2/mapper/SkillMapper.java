package com.backend_core.recruforce2.mapper;

import com.backend_core.recruforce2.domain.entities.Skill;
import com.backend_core.recruforce2.dto.response.SkillResponse;
import org.mapstruct.Mapper;

/**
 * MapStruct mapper for Skill entity ↔ DTOs conversion.
 */
@Mapper(componentModel = "spring")
public interface SkillMapper {

  /**
   * Converts Skill entity to SkillResponse DTO.
   */
  SkillResponse toResponse(Skill skill);
}
