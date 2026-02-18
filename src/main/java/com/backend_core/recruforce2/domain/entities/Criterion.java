package com.backend_core.recruforce2.domain.entities;

import com.backend_core.recruforce2.domain.enums.InterviewType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Represents an evaluation criterion used during interviews.
 * Criteria are typed by interview phase (Soft Skills or Hard Skills).
 */
@Entity
@Table(name = "criteria", indexes = {
  @Index(name = "idx_criteria_interview_type", columnList = "interview_type")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Criterion {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "Criterion name is required")
  @Size(max = 150)
  @Column(nullable = false, length = 150)
  private String name;

  @Column(columnDefinition = "TEXT")
  private String description;

  /** The interview phase this criterion applies to */
  @Enumerated(EnumType.STRING)
  @Column(name = "interview_type", nullable = false, length = 20)
  private InterviewType interviewType;

  /** Weight of this criterion in the overall score (0 to 100) */
  @Min(0)
  @Max(100)
  @Column(nullable = false)
  @Builder.Default
  private Integer weight = 1;

  /** Whether this criterion is currently in use */
  @Column(name = "is_active", nullable = false)
  @Builder.Default
  private Boolean isActive = true;
}
