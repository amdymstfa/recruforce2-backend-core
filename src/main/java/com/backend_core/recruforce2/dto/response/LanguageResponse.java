package com.backend_core.recruforce2.dto.response;

import com.backend_core.recruforce2.domain.enums.LanguageLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LanguageResponse {
  private Long id;
  private String name;
  private LanguageLevel level;
}
