package com.backend_core.recruforce2.repository;

import com.backend_core.recruforce2.domain.entities.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * JPA repository for {@link AuditLog} entity.
 * Provides access to the {@code audit_logs} table in PostgreSQL.
 * All records are immutable — no update or delete operations.
 */
@Repository
public interface RecruiterAvailabilityRepository extends JpaRepository<AuditLog, Long> {

  /** Finds all audit logs for a specific user (paginated, most recent first) */
  Page<AuditLog> findByUserIdOrderByTimestampDesc(Long userId, Pageable pageable);

  /** Finds all audit logs for a specific entity type and ID */
  List<AuditLog> findByEntityTypeAndEntityIdOrderByTimestampDesc(String entityType, Long entityId);

  /** Finds audit logs by action type */
  Page<AuditLog> findByActionOrderByTimestampDesc(String action, Pageable pageable);

  /** Finds audit logs within a time range */
  @Query("SELECT a FROM AuditLog a WHERE " +
    "a.timestamp BETWEEN :from AND :to " +
    "ORDER BY a.timestamp DESC")
  Page<AuditLog> findBetweenDates(@Param("from") LocalDateTime from,
                                  @Param("to") LocalDateTime to,
                                  Pageable pageable);

  /** Finds audit logs for a specific user and action */
  List<AuditLog> findByUserIdAndActionOrderByTimestampDesc(Long userId, String action);

  /** Finds the most recent audit log entries (for live monitoring) */
  @Query("SELECT a FROM AuditLog a ORDER BY a.timestamp DESC")
  Page<AuditLog> findLatest(Pageable pageable);

  /** Counts audit log entries by action type */
  long countByAction(String action);

  /** Counts audit log entries for a specific user */
  long countByUserId(Long userId);
}
