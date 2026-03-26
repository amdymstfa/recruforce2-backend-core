package com.backend_core.recruforce2.repository;

import com.backend_core.recruforce2.domain.entities.Criterion;
import com.backend_core.recruforce2.domain.enums.InterviewType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CriterionRepository extends JpaRepository<Criterion, Long> {

  List<Criterion> findByInterviewType(InterviewType interviewType);

  List<Criterion> findByIsActiveTrue();

  List<Criterion> findByInterviewTypeAndIsActiveTrue(InterviewType interviewType);
}
