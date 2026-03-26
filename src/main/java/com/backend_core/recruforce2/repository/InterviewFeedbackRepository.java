package com.backend_core.recruforce2.repository;

import com.backend_core.recruforce2.domain.entities.Feedback;
import com.backend_core.recruforce2.domain.enums.InterviewType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA repository for {@link Feedback} entity.
 * Provides access to the {@code feedbacks} table in PostgreSQL.
 */
@Repository
public interface InterviewFeedbackRepository extends JpaRepository<Feedback, Long> {

  /** Finds the feedback for a specific interview */
  Optional<Feedback> findByInterviewId(Long interviewId);

  /** Checks if feedback already exists for an interview */
  boolean existsByInterviewId(Long interviewId);

  /** Finds all feedbacks submitted by a specific evaluator */
  List<Feedback> findByEvaluatorId(Long evaluatorId);

  /** Finds all feedbacks for a specific candidate across all interviews */
  @Query("SELECT f FROM Feedback f " +
    "JOIN f.interview i " +
    "JOIN i.application a " +
    "WHERE a.candidate.id = :candidateId " +
    "ORDER BY f.createdAt DESC")
  List<Feedback> findByCandidateId(@Param("candidateId") Long candidateId);

  /** Finds all feedbacks for a specific job offer */
  @Query("SELECT f FROM Feedback f " +
    "JOIN f.interview i " +
    "JOIN i.application a " +
    "WHERE a.jobOffer.id = :jobOfferId")
  List<Feedback> findByJobOfferId(@Param("jobOfferId") Long jobOfferId);

  /** Finds feedbacks by interview type (SOFT_SKILLS or HARD_SKILLS) */
  @Query("SELECT f FROM Feedback f " +
    "JOIN f.interview i " +
    "WHERE i.type = :type AND i.application.id = :applicationId")
  Optional<Feedback> findByApplicationIdAndInterviewType(
    @Param("applicationId") Long applicationId,
    @Param("type") InterviewType type);

  /** Calculates the average overall score for a specific job offer */
  @Query("SELECT AVG(f.overallScore) FROM Feedback f " +
    "JOIN f.interview i " +
    "JOIN i.application a " +
    "WHERE a.jobOffer.id = :jobOfferId AND f.overallScore IS NOT NULL")
  Double averageScoreByJobOffer(@Param("jobOfferId") Long jobOfferId);

  /** Finds top-rated candidates for a job offer (score >= threshold) */
  @Query("SELECT f FROM Feedback f " +
    "JOIN f.interview i " +
    "JOIN i.application a " +
    "WHERE a.jobOffer.id = :jobOfferId AND f.overallScore >= :threshold " +
    "ORDER BY f.overallScore DESC")
  List<Feedback> findTopRatedByJobOffer(@Param("jobOfferId") Long jobOfferId,
                                        @Param("threshold") Integer threshold);

  @Query("SELECT AVG(f.overallScore) FROM Feedback f WHERE f.overallScore IS NOT NULL")
  Double findAverageOverallScore();
}
