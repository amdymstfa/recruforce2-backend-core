package com.backend_core.recruforce2.dto.response;

import com.backend_core.recruforce2.domain.enums.InterviewStatus;
import com.backend_core.recruforce2.domain.enums.InterviewType;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Response DTO for interview information.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewResponse {
  private Long id;
  private Long applicationId;
  private String candidateName;
  private String candidateEmail;
  private String interviewerName;
  private String interviewerEmail;
  private InterviewType type;
  private InterviewStatus status;
  private LocalDateTime dateTime;
  private Integer durationMinutes;
  private String location;
  private String videoLink;
  private Long interviewerId;
  private String invitationToken;
  private LocalDateTime confirmationDate;
  private boolean hasFeedback;
}
