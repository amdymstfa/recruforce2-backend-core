package com.backend_core.recruforce2.dto.response;

import com.backend_core.recruforce2.domain.enums.NotificationType;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Response DTO for notification information.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {

  private Long id;
  private NotificationType type;
  private String title;
  private String message;
  private String link;
  private Boolean isRead;
  private LocalDateTime createdAt;
  private LocalDateTime readAt;
}
