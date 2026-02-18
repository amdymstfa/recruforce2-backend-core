package com.backend_core.recruforce2.domain.entities;

import com.backend_core.recruforce2.domain.enums.SkillLevel;
import com.backend_core.recruforce2.domain.enums.SkillType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Represents a skill in the platform's skill repository.
 */
@Entity
@Table(name = "skills", indexes = {
  @Index(name = "idx_skills_name", columnList = "name"),
  @Index(name = "idx_skills_type", columnList = "type")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Skill {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "Skill name is required")
  @Size(max = 100)
  @Column(nullable = false, length = 100)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private SkillType type;

  @Enumerated(EnumType.STRING)
  @Column(name = "required_level", length = 20)
  private SkillLevel requiredLevel;
}
