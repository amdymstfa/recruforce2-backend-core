package com.backend_core.recruforce2.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the feedback submitted by an interviewer after an interview.
 * Aggregates individual criterion scores into an overall evaluation.
 */
@Entity
@Table(name = "feedbacks", indexes = {
  @Index(name = "idx_feedbacks_interview_id", columnList = "interview_id"),
  @Index(name = "idx_feedbacks_evaluator_id", columnList = "evaluator_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Feedback {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /** The interview this feedback is for */
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "interview_id", nullable = false, unique = true)
  private Interview interview;

  /** The recruiter or manager who submitted the feedback */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "evaluator_id", nullable = false)
  private User evaluator;

  /** Overall score for the interview (0 to 100) */
  @Min(0)
  @Max(100)
  @Column(name = "overall_score")
  private Integer overallScore;

  /** General comments about the candidate */
  @Column(name = "general_comment", columnDefinition = "TEXT")
  private String generalComment;

  /** Final recommendation for this candidate */
  @Column(length = 500)
  private String recommendation;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  /** Individual evaluations per criterion */
  @OneToMany(mappedBy = "feedback", cascade = CascadeType.ALL,
    orphanRemoval = true, fetch = FetchType.LAZY)
  @Builder.Default
  private List<EvaluationCriterion> criteriaEvaluations = new ArrayList<>();

  // -------------------------------------------------------
  // Lifecycle hooks
  // -------------------------------------------------------

  @PrePersist
  protected void onCreate() {
    this.createdAt = LocalDateTime.now();
  }

  // -------------------------------------------------------
  // Business methods
  // -------------------------------------------------------

  /** Adds a criterion evaluation to this feedback */
  public void add(EvaluationCriterion evaluation) {
    criteriaEvaluations.add(evaluation);
    evaluation.setFeedback(this);
  }

  /** Updates the general comment and recommendation */
  public void update(String generalComment, String recommendation) {
    this.generalComment = generalComment;
    this.recommendation = recommendation;
  }

  /**
   * Calculates the overall score as a weighted average of criterion evaluations.
   * Updates the overallScore field.
   */
  public void calculateOverallScore() {
    if (criteriaEvaluations == null || criteriaEvaluations.isEmpty()) {
      this.overallScore = 0;
      return;
    }
    double totalWeight = criteriaEvaluations.stream()
      .mapToInt(e -> e.getCriterion().getWeight())
      .sum();
    double weightedSum = criteriaEvaluations.stream()
      .mapToDouble(e -> (double) e.getScore() * e.getCriterion().getWeight())
      .sum();
    this.overallScore = totalWeight > 0
      ? (int) Math.round(weightedSum / totalWeight)
      : 0;
  }
}
