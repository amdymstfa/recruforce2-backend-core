package com.backend_core.recruforce2.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExperienceRequest {
  private String position;
  private String company;
  private String description;
  private boolean is_current;
}
