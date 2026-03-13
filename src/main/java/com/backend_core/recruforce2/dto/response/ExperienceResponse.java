package com.backend_core.recruforce2.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExperienceResponse {
  private Long id;
  private String position;
  private String company;
  private String description;
  private LocalDate startDate;
  private LocalDate endDate;
  private Boolean isCurrent;
}
