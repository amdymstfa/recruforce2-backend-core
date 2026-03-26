package com.backend_core.recruforce2.repository;

import com.backend_core.recruforce2.domain.entities.Candidate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * JPA repository for {@link Candidate} entity.
 * Provides access to the {@code candidates} table in PostgreSQL.
 * Note: parsed CV content is stored in MongoDB — use {@link ParsedCvMongoRepository} for that.
 */
@Repository
public interface CandidateProfileRepository extends JpaRepository<Candidate, Long> {

  /** Finds a candidate by their email address */
  Optional<Candidate> findByEmail(String email);

  /** Checks if a candidate with the given email already exists */
  boolean existsByEmail(String email);

  /** Finds a candidate by their MongoDB parsed CV document ID */
  Optional<Candidate> findByParsedCvId(String parsedCvId);

  /** Searches candidates by first name or last name (case-insensitive, paginated) */
  @Query("SELECT c FROM Candidate c WHERE " +
    "LOWER(c.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
    "LOWER(c.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
    "LOWER(c.email) LIKE LOWER(CONCAT('%', :keyword, '%'))")
  Page<Candidate> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

  /** Finds candidates who have applied to a specific job offer */
  @Query("SELECT DISTINCT c FROM Candidate c " +
    "JOIN c.applications a " +
    "WHERE a.jobOffer.id = :jobOfferId")
  Page<Candidate> findByJobOfferId(@Param("jobOfferId") Long jobOfferId, Pageable pageable);

  /** Finds candidates with a parsed CV document (AI-processed) */
  @Query("SELECT c FROM Candidate c WHERE c.parsedCvId IS NOT NULL")
  Page<Candidate> findAllWithParsedCv(Pageable pageable);

  /** Counts the total number of candidates */
  long count();

  long countByParsedCvIdIsNotNull();
}
