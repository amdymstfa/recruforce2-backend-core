package com.backend_core.recruforce2.domain.enums;

/**
 * Tracks the current state of a scheduled interview.
 */
public enum InterviewStatus {

  SCHEDULED,

  CONFIRMED,

  WAITING_CANDIDATE,

  COMPLETED,

  CANCELLED,

  RESCHEDULED
}
