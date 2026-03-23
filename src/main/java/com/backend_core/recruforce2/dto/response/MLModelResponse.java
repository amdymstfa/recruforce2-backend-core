package com.backend_core.recruforce2.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MLModelResponse {
  private Long id;
  private String name;
  private String version;
  private String algorithm;
  private LocalDateTime trainingDate;
  private Double accuracy;
  private Double precision;
  private Double recall;
  private Double f1Score;
  private Integer predictionsCount;
  private Double successRate;
  private Boolean isActive;
  private String statistics;
}
