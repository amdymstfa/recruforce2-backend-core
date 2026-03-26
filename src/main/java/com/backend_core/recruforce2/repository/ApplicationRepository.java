package com.backend_core.recruforce2.repository;

import com.backend_core.recruforce2.domain.entities.Application;
import com.backend_core.recruforce2.domain.entities.JobOffer;
import com.backend_core.recruforce2.domain.enums.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * JPA repository for {@link Application} entity.
 * Provides access to the {@code applications} table in PostgreSQL.
 */
@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

  /** Finds all applications for a specific job offer (paginated) */
  Page<Application> findByJobOfferId(Long jobOfferId, Pageable pageable);

  /** Finds all applications submitted by a specific candidate */
  List<Application> findByCandidateId(Long candidateId);

  /** Finds a specific application by candidate and job offer */
  Optional<Application> findByCandidateIdAndJobOfferId(Long candidateId, Long jobOfferId);

  /** Checks if a candidate has already applied to a job offer */
  boolean existsByCandidateIdAndJobOfferId(Long candidateId, Long jobOfferId);

  /** Finds applications by status (paginated) */
  Page<Application> findByStatus(ApplicationStatus status, Pageable pageable);

  /** Finds applications for a job offer filtered by status */
  List<Application> findByJobOfferIdAndStatus(Long jobOfferId, ApplicationStatus status);

  /** Finds qualified applications for a job offer, ordered by matching score descending */
  @Query("SELECT a FROM Application a WHERE " +
    "a.jobOffer.id = :jobOfferId AND " +
    "a.isQualified = true " +
    "ORDER BY a.matchingScore DESC")
  List<Application> findQualifiedByJobOffer(@Param("jobOfferId") Long jobOfferId);

  /** Finds recent applications received after a given timestamp */
  List<Application> findByReceivedAtAfter(LocalDateTime since);

  /** Finds applications with a matching score above a threshold */
  @Query("SELECT a FROM Application a WHERE " +
    "a.jobOffer.id = :jobOfferId AND " +
    "a.matchingScore >= :threshold " +
    "ORDER BY a.matchingScore DESC")
  List<Application> findByJobOfferIdAndScoreAbove(@Param("jobOfferId") Long jobOfferId,
                                                  @Param("threshold") Integer threshold);

  /** Counts applications by status for a specific job offer */
  long countByJobOfferIdAndStatus(Long jobOfferId, ApplicationStatus status);

  /** Counts all applications received today */
  @Query("SELECT COUNT(a) FROM Application a WHERE a.receivedAt >= :startOfDay")
  long countReceivedToday(@Param("startOfDay") LocalDateTime startOfDay);

  /** Counts total applications per job offer for dashboard stats */
  @Query("SELECT a.jobOffer.id, COUNT(a) FROM Application a " +
    "GROUP BY a.jobOffer.id")
  List<Object[]> countPerJobOffer();

  @Query("SELECT COUNT(a) FROM Application a WHERE a.receivedAt >= CURRENT_DATE")
  long countApplicationsToday();

  @Query("SELECT AVG(a.matchingScore) FROM Application a WHERE a.matchingScore IS NOT NULL")
  Double getAverageMatchingScore();

  // Pour le Map (Status -> Count)
  @Query("SELECT a.status, COUNT(a) FROM Application a GROUP BY a.status")
  List<Object[]> countApplicationsByStatus();

  long countByIsQualifiedTrue();

  long countByStatus(ApplicationStatus status);

  List<Application> findJobOfferById(Long id);
}
