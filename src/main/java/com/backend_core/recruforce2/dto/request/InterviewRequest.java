package com.backend_core.recruforce2.dto.request;

import com.backend_core.recruforce2.domain.enums.InterviewType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Request DTO for scheduling an interview.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewRequest {

  @NotNull(message = "Application ID is required")
  private Long applicationId;

  @NotNull(message = "Interview type is required")
  private InterviewType type;

  @NotNull(message = "Interviewer ID is required")
  private Long interviewerId;

  @NotNull(message = "Date and time are required")
  private LocalDateTime dateTime;

  private Integer durationMinutes;

  private String location;

  private String videoLink;
}
