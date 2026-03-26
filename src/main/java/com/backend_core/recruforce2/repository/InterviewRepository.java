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

@Repository
public interface InterviewRepository extends JpaRepository<Interview, Long> {

  @Query("SELECT COUNT(i) FROM Interview i WHERE i.dateTime > CURRENT_TIMESTAMP AND i.status != 'CANCELLED'")
  long countUpcomingInterviews();

  long countByStatus(InterviewStatus status);

  @Query("SELECT i.status, COUNT(i) FROM Interview i GROUP BY i.status")
  List<Object[]> countInterviewsByStatus();

  Optional<Interview> findByApplicationIdAndType(Long applicationId, InterviewType type);

  Optional<Interview> findByInvitationToken(String token);

  List<Interview> findByApplicationId(Long applicationId);

  @Query("SELECT i FROM Interview i WHERE i.dateTime BETWEEN :start AND :end AND i.reminderSent = false")
  List<Interview> findNeedingReminders(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

  List<Interview> findByInterviewerId(Long interviewerId);

  @Query("SELECT i FROM Interview i WHERE i.interviewer.id = :interviewerId AND i.dateTime >= :start ORDER BY i.dateTime ASC")
  List<Interview> findUpcomingByInterviewer(@Param("interviewerId") Long interviewerId, @Param("start") LocalDateTime start);

}
