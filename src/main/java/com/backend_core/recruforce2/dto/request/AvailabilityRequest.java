package com.backend_core.recruforce2.dto.request;

import com.backend_core.recruforce2.domain.enums.WeekDay;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Request DTO for creating or updating availability slots.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvailabilityRequest {

  @NotNull(message = "Start date and time are required")
  private LocalDateTime startDateTime;

  @NotNull(message = "End date and time are required")
  private LocalDateTime endDateTime;

  private Boolean isRecurring;

  private WeekDay dayOfWeek;
}
