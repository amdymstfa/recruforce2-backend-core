package com.backend_core.recruforce2.exception;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Standard error response structure returned by the API.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {

  private LocalDateTime timestamp;
  private int status;
  private String error;
  private String message;
  private Map<String, String> details;
}
