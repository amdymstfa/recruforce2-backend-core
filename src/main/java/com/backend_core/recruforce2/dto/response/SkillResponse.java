package com.backend_core.recruforce2.dto.response;

import com.backend_core.recruforce2.domain.enums.SkillLevel;
import com.backend_core.recruforce2.domain.enums.SkillType;
import lombok.*;

/**
 * Response DTO for skill information.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillResponse {

  private Long id;
  private String name;
  private SkillType type;
  private SkillLevel requiredLevel;
}
