package com.backend_core.recruforce2.repository;

import com.backend_core.recruforce2.domain.entities.Interview;
import com.backend_core.recruforce2.domain.enums.InterviewStatus;
import com.backend_core.recruforce2.domain.enums.InterviewType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * JPA repository for {@link Interview} entity.
 * Provides access to the {@code interviews} table in PostgreSQL.
 */
@Repository
public interface InterviewRepository extends JpaRepository<Interview, Long> {

  /** Finds all interviews for a specific application */
  List<Interview> findByApplicationId(Long applicationId);

  /** Finds a specific interview by application and type */
  Optional<Interview> findByApplicationIdAndType(Long applicationId, InterviewType type);

  /** Finds all interviews assigned to a specific interviewer */
  List<Interview> findByInterviewerId(Long interviewerId);

  /** Finds interviews by status */
  List<Interview> findByStatus(InterviewStatus status);

  /** Finds interviews by invitation token (candidate confirmation link) */
  Optional<Interview> findByInvitationToken(String token);

  /** Finds upcoming interviews for a specific interviewer within a time range */
  @Query("SELECT i FROM Interview i WHERE " +
    "i.interviewer.id = :interviewerId AND " +
    "i.dateTime BETWEEN :from AND :to AND " +
    "i.status NOT IN ('CANCELLED', 'RESCHEDULED') " +
    "ORDER BY i.dateTime ASC")
  List<Interview> findUpcomingByInterviewer(@Param("interviewerId") Long interviewerId,
                                            @Param("from") LocalDateTime from,
                                            @Param("to") LocalDateTime to);

  /** Finds all interviews scheduled within a date range */
  @Query("SELECT i FROM Interview i WHERE " +
    "i.dateTime BETWEEN :from AND :to " +
    "ORDER BY i.dateTime ASC")
  List<Interview> findBetweenDates(@Param("from") LocalDateTime from,
                                   @Param("to") LocalDateTime to);

  /** Finds interviews needing reminders (confirmed, scheduled within next 24h) */
  @Query("SELECT i FROM Interview i WHERE " +
    "i.status = 'CONFIRMED' AND " +
    "i.dateTime BETWEEN :now AND :in24h")
  List<Interview> findNeedingReminders(@Param("now") LocalDateTime now,
                                       @Param("in24h") LocalDateTime in24h);

  /** Counts interviews by status */
  long countByStatus(InterviewStatus status);

  /** Counts interviews by type and status */
  long countByTypeAndStatus(InterviewType type, InterviewStatus status);
}
