package com.backend_core.recruforce2.repository;

import com.backend_core.recruforce2.domain.entities.Skill;
import com.backend_core.recruforce2.domain.enums.SkillType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA repository for {@link Skill} entity.
 * Provides access to the {@code skills} table in PostgreSQL.
 */
@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {

  /** Finds a skill by its exact name */
  Optional<Skill> findByName(String name);

  Optional<Skill> findByNameIgnoreCase(String name);

  /** Checks if a skill with the given name already exists */
  boolean existsByName(String name);

  /** Finds all skills of a specific type */
  List<Skill> findByType(SkillType type);

  /** Searches skills by name (case-insensitive) */
  @Query("SELECT s FROM Skill s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
  List<Skill> searchByName(@Param("keyword") String keyword);

  /** Finds all skills associated with a specific job offer */
  @Query("SELECT j.requiredSkills FROM JobOffer j WHERE j.id = :jobOfferId")
  List<Skill> findByJobOfferId(@Param("jobOfferId") Long jobOfferId);

  /** Finds skills by type ordered by name */
  List<Skill> findByTypeOrderByNameAsc(SkillType type);

  /** Counts skills by type */
  long countByType(SkillType type);
}
