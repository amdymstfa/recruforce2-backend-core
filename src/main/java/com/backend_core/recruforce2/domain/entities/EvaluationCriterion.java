package com.backend_core.recruforce2.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

/**
 * Represents the score given to a single criterion during an interview evaluation.
 * Links a Feedback to a Criterion with a numeric score and optional comment.
 */
@Entity
@Table(name = "evaluation_criteria", indexes = {
  @Index(name = "idx_eval_criteria_feedback_id", columnList = "feedback_id"),
  @Index(name = "idx_eval_criteria_criterion_id", columnList = "criterion_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluationCriterion {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /** The feedback this evaluation belongs to */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "feedback_id", nullable = false)
  private Feedback feedback;

  /** The criterion being evaluated */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "criterion_id", nullable = false)
  private Criterion criterion;

  /** Score awarded for this criterion (0 to 10) */
  @Min(0)
  @Max(10)
  @Column(nullable = false)
  private Integer score;

  /** Optional comment on this specific criterion */
  @Column(columnDefinition = "TEXT")
  private String comment;
}
