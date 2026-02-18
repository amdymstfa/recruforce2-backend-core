package com.backend_core.recruforce2.domain.entities;

import com.backend_core.recruforce2.domain.enums.LanguageLevel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Represents a language spoken by a candidate, with a proficiency level.
 */
@Entity
@Table(name = "languages", indexes = {
  @Index(name = "idx_languages_candidate_id", columnList = "candidate_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Language {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "Language name is required")
  @Size(max = 100)
  @Column(nullable = false, length = 100)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private LanguageLevel level;

  /** The candidate this language belongs to */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "candidate_id", nullable = false)
  private Candidate candidate;
}
