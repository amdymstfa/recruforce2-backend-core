package com.backend_core.recruforce2.repository;

import com.backend_core.recruforce2.domain.entities.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

  Optional<Feedback> findByInterviewId(Long interviewId);

  @Query("SELECT AVG(f.overallScore) FROM Feedback f WHERE f.overallScore IS NOT NULL")
  Double findAverageOverallScore();

}
