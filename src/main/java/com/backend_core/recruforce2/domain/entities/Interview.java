package com.backend_core.recruforce2.domain.entities;

import com.backend_core.recruforce2.domain.enums.InterviewStatus;
import com.backend_core.recruforce2.domain.enums.InterviewType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a scheduled interview between a candidate and an interviewer.
 * Supports two phases: Soft Skills (Phase 1) and Hard Skills (Phase 2).
 */
@Entity
@Table(name = "interviews", indexes = {
  @Index(name = "idx_interviews_application_id", columnList = "application_id"),
  @Index(name = "idx_interviews_interviewer_id", columnList = "interviewer_id"),
  @Index(name = "idx_interviews_status", columnList = "status"),
  @Index(name = "idx_interviews_date", columnList = "date_time")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Interview {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /** The application this interview is associated with */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "application_id", nullable = false)
  private Application application;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private InterviewType type;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 25)
  @Builder.Default
  private InterviewStatus status = InterviewStatus.SCHEDULED;

  /** Scheduled date and time of the interview */
  @Column(name = "date_time")
  private LocalDateTime dateTime;

  /** Duration of the interview in minutes */
  @Column(name = "duration_minutes")
  @Builder.Default
  private Integer durationMinutes = 60;

  /** Location or room (physical or "Remote") */
  @Column(length = 250)
  private String location;

  /** Video conference link (Google Meet, Teams, Zoom...) */
  @Column(name = "video_link", length = 500)
  private String videoLink;

  /** The recruiter or manager conducting the interview */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "interviewer_id", nullable = false)
  private User interviewer;

  @Column(name = "reminder_sent")
  @Builder.Default
  private Boolean reminderSent = false;

  /**
   * Unique token sent to the candidate to confirm their slot selection.
   * Generated automatically on creation.
   */
  @Column(name = "invitation_token", unique = true, length = 100)
  private String invitationToken;

  /** Date and time when the candidate confirmed their slot */
  @Column(name = "confirmation_date")
  private LocalDateTime confirmationDate;

  /** Feedback submitted after the interview */
  @OneToOne(mappedBy = "interview", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private Feedback feedback;

  // -------------------------------------------------------
  // Lifecycle hooks
  // -------------------------------------------------------

  @PrePersist
  protected void onCreate() {
    this.invitationToken = UUID.randomUUID().toString();
  }

  // -------------------------------------------------------
  // Business methods
  // -------------------------------------------------------

  /** Schedules the interview at the given date/time */
  public void schedule(LocalDateTime dateTime, String location, String videoLink) {
    this.dateTime = dateTime;
    this.location = location;
    this.videoLink = videoLink;
    this.status = InterviewStatus.SCHEDULED;
  }

  /** Confirms the interview (typically triggered by the candidate) */
  public void confirm() {
    this.status = InterviewStatus.CONFIRMED;
    this.confirmationDate = LocalDateTime.now();
  }

  /** Cancels the interview */
  public void cancel() {
    this.status = InterviewStatus.CANCELLED;
  }

  /** Reschedules the interview to a new date/time */
  public void reschedule(LocalDateTime newDateTime) {
    this.dateTime = newDateTime;
    this.status = InterviewStatus.RESCHEDULED;
    this.confirmationDate = null;
  }

  /** Sends a reminder — actual email sending is delegated to EmailService */
  public void sendReminder() {
    // Trigger point for EmailService.sendInterviewReminder(this)
  }
}
