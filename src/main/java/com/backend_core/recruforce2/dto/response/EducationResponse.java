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
public class EducationResponse {
  private Long id;
  private String degree;
  private String institution;
  private String field;
  private LocalDate startDate;
  private LocalDate endDate;
  private Integer yearsObtained;
}
