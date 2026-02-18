package com.backend_core.recruforce2.domain.entities;

import com.backend_core.recruforce2.domain.enums.SkillLevel;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

/**
 * Represents a skill owned by a candidate, with a proficiency level and years of experience.
 */
@Entity
@Table(name = "candidate_skills", indexes = {
  @Index(name = "idx_candidate_skills_candidate_id", columnList = "candidate_id"),
  @Index(name = "idx_candidate_skills_skill_id", columnList = "skill_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateSkill {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /** The candidate who owns this skill */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "candidate_id", nullable = false)
  private Candidate candidate;

  /** The skill reference */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "skill_id", nullable = false)
  private Skill skill;

  /** Candidate's proficiency level for this skill */
  @Enumerated(EnumType.STRING)
  @Column(name = "mastery_level", nullable = false, length = 20)
  private SkillLevel masteryLevel;

  /** Number of years of experience with this skill */
  @Min(0)
  @Column(name = "years_experience")
  private Integer yearsExperience;
}
