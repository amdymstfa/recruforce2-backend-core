package com.backend_core.recruforce2.repository;

import com.backend_core.recruforce2.domain.entities.JobOffer;
import com.backend_core.recruforce2.domain.enums.ContractType;
import com.backend_core.recruforce2.domain.enums.OfferStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * JPA repository for {@link JobOffer} entity.
 * Provides access to the {@code job_offers} table in PostgreSQL.
 */
@Repository
public interface JobOfferRepository extends JpaRepository<JobOffer, Long> {

  /** Finds all job offers with a specific status (paginated) */
  Page<JobOffer> findByStatus(OfferStatus status, Pageable pageable);

  /** Finds all active job offers */
  List<JobOffer> findByStatus(OfferStatus status);

  /** Finds all job offers created by a specific recruiter */
  Page<JobOffer> findByCreatedById(Long userId, Pageable pageable);

  /** Finds all job offers expiring before a given date */
  List<JobOffer> findByExpirationDateBeforeAndStatus(LocalDate date, OfferStatus status);

  /** Finds job offers by contract type */
  Page<JobOffer> findByContractType(ContractType contractType, Pageable pageable);

  /** Searches job offers by title or description (case-insensitive, paginated) */
  @Query("SELECT j FROM JobOffer j WHERE " +
    "LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
    "LOWER(j.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
  Page<JobOffer> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

  /** Finds active offers with filters (location + contract type) */
  @Query("SELECT j FROM JobOffer j WHERE " +
    "j.status = :status AND " +
    "(:location IS NULL OR LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
    "(:contractType IS NULL OR j.contractType = :contractType)")
  Page<JobOffer> findWithFilters(@Param("status") OfferStatus status,
                                 @Param("location") String location,
                                 @Param("contractType") ContractType contractType,
                                 Pageable pageable);

  /** Counts job offers by status */
  long countByStatus(OfferStatus status);

  /** Counts job offers created by a specific recruiter */
  long countByCreatedById(Long userId);

  /** Finds job offers published on LinkedIn */
  List<JobOffer> findByPublishedOnLinkedinTrue();

  /** Checks if a LinkedIn job ID already exists */
  boolean existsByLinkedinJobId(String linkedinJobId);
}
