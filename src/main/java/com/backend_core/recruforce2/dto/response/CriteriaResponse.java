package com.backend_core.recruforce2.dto.response;

import com.backend_core.recruforce2.domain.enums.InterviewType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CriteriaResponse {

  private Long id;
  private String name;
  private String description;
  private InterviewType interviewType;
  private Integer weight;
  private Boolean isActive;
}
