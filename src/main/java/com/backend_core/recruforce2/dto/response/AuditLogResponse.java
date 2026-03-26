package com.backend_core.recruforce2.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogResponse {
  private Long id;
  private Long userId;
  private String userName;
  private String action;
  private String entityType;
  private Long entityId;
  private String details;
  private String ipAddress;
  private LocalDateTime timestamp;
}
