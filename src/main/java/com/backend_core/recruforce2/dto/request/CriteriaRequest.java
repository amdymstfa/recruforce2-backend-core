package com.backend_core.recruforce2.dto.request;

import com.backend_core.recruforce2.domain.enums.InterviewType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CriteriaRequest {

  @NotBlank
  private String name;

  private String description;

  private InterviewType interviewType;

  @Min(0)
  @Max(100)
  private Integer weight;

  private Boolean isActive;
}
