package com.backend_core.recruforce2.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

/**
 * Represents a professional experience entry in a candidate's profile.
 */
@Entity
@Table(name = "experiences", indexes = {
  @Index(name = "idx_experiences_candidate_id", columnList = "candidate_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Experience {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "Position is required")
  @Size(max = 150)
  @Column(nullable = false, length = 150)
  private String position;

  @NotBlank(message = "Company name is required")
  @Size(max = 150)
  @Column(nullable = false, length = 150)
  private String company;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(name = "start_date")
  private LocalDate startDate;

  @Column(name = "end_date")
  private LocalDate endDate;

  /** True if this is the candidate's current position */
  @Column(name = "is_current", nullable = false)
  @Builder.Default
  private Boolean isCurrent = false;

  /** The candidate this experience belongs to */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "candidate_id", nullable = false)
  private Candidate candidate;
}
