package com.backend_core.recruforce2.domain.entities;

import com.backend_core.recruforce2.domain.enums.ApplicationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Represents a candidate's application for a specific job offer.
 * Tracks the full application lifecycle including AI matching score.
 */
@Entity
@Table(name = "applications", indexes = {
  @Index(name = "idx_applications_candidate_id", columnList = "candidate_id"),
  @Index(name = "idx_applications_job_offer_id", columnList = "job_offer_id"),
  @Index(name = "idx_applications_status", columnList = "status"),
  @Index(name = "idx_applications_created_at", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Application {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /** The candidate who submitted this application */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "candidate_id", nullable = false)
  private Candidate candidate;

  /** The job offer this application targets */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "job_offer_id", nullable = false)
  private JobOffer jobOffer;

  @Column(name = "received_at", nullable = false, updatable = false)
  private LocalDateTime receivedAt;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 30)
  @Builder.Default
  private ApplicationStatus status = ApplicationStatus.RECEIVED;

  /**
   * AI-generated matching score between the candidate profile and the job offer.
   * Range: 0.0 to 100.0
   */
  @Column(name = "matching_score")
  private Integer matchingScore;

  /** Whether the candidate is pre-qualified based on the matching score threshold */
  @Column(name = "is_qualified", nullable = false)
  @Builder.Default
  private Boolean isQualified = false;

  /** Path to the CV file submitted with this application */
  @Column(name = "cv_file_path", length = 500)
  private String cvFilePath;

  /** Optional cover letter text */
  @Column(name = "cover_letter", columnDefinition = "TEXT")
  private String coverLetter;

  /** Source channel: EMAIL, WEB_FORM, LINKEDIN, etc. */
  @Column(name = "source_channel", length = 50)
  private String sourceChannel;

  // -------------------------------------------------------
  // Lifecycle hooks
  // -------------------------------------------------------

  @PrePersist
  protected void onCreate() {
    this.receivedAt = LocalDateTime.now();
  }

  // -------------------------------------------------------
  // Business methods
  // -------------------------------------------------------

  /** Submits the application */
  public void submit() {
    this.status = ApplicationStatus.RECEIVED;
    this.receivedAt = LocalDateTime.now();
  }

  /** Analyzes and updates the AI matching score and qualification flag */
  public void analyzeScore(Integer score, Integer threshold) {
    this.matchingScore = score;
    this.isQualified = score != null && score >= threshold;
  }

  /** Calculates the score — delegates to AI service (called externally) */
  public Integer calculateScore() {
    return this.matchingScore;
  }

  /** Changes the application status */
  public void changeStatus(ApplicationStatus newStatus) {
    this.status = newStatus;
  }
}
