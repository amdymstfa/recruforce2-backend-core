package com.backend_core.recruforce2.dto.response;


import com.backend_core.recruforce2.domain.enums.SkillType;
import com.backend_core.recruforce2.domain.enums.SkillLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CandidateSkillResponse {
  private Long id;
  private String name;
  private SkillType type;
  private SkillLevel masteryLevel;
  private Integer yearsExperience;
}
