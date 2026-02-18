package com.backend_core.recruforce2.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

/**
 * Represents an education entry in a candidate's profile.
 */
@Entity
@Table(name = "educations", indexes = {
  @Index(name = "idx_educations_candidate_id", columnList = "candidate_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Education {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "Degree is required")
  @Size(max = 150)
  @Column(nullable = false, length = 150)
  private String degree;

  @NotBlank(message = "Institution name is required")
  @Size(max = 150)
  @Column(nullable = false, length = 150)
  private String institution;

  @Size(max = 150)
  @Column(length = 150)
  private String field;

  @Column(name = "start_date")
  private LocalDate startDate;

  @Column(name = "end_date")
  private LocalDate endDate;

  /** Years of study duration */
  @Column(name = "years_obtained")
  private Integer yearsObtained;

  /** The candidate this education entry belongs to */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "candidate_id", nullable = false)
  private Candidate candidate;
}
