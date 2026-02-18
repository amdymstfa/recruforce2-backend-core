package com.backend_core.recruforce2.domain.entities;

import jakarta.persistence.*;
import lombok.*;

/**
 * Stores per-user notification preferences.
 */
@Entity
@Table(name = "notification_preferences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationPreferences {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /** The user these preferences belong to */
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false, unique = true)
  private User user;

  /** Receive email notification when a new application is submitted */
  @Column(name = "email_new_candidate", nullable = false)
  @Builder.Default
  private Boolean emailNewCandidate = true;

  /** Receive email notification when an interview is scheduled */
  @Column(name = "email_interview", nullable = false)
  @Builder.Default
  private Boolean emailInterview = true;

  /** Receive email notification when a recruitment deadline is approaching */
  @Column(name = "email_deadline", nullable = false)
  @Builder.Default
  private Boolean emailDeadline = true;

  /** Receive in-app notifications */
  @Column(name = "in_app_notifications", nullable = false)
  @Builder.Default
  private Boolean inAppNotifications = true;

  // -------------------------------------------------------
  // Business methods
  // -------------------------------------------------------

  /** Updates all preferences at once */
  public void updatePreferences(Boolean emailNewCandidate,
                                Boolean emailInterview,
                                Boolean emailDeadline,
                                Boolean inAppNotifications) {
    this.emailNewCandidate = emailNewCandidate;
    this.emailInterview = emailInterview;
    this.emailDeadline = emailDeadline;
    this.inAppNotifications = inAppNotifications;
  }
}
