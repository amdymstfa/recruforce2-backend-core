package com.backend_core.recruforce2.mongo.repository;

import com.backend_core.recruforce2.mongo.document.AiLogDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * MongoDB repository for {@link AiLogDocument}.
 * Provides access to the {@code ai_logs} collection in MongoDB.
 * <p>
 * Every call to the Python AI microservice generates one log entry.
 * Used for debugging, performance monitoring, and AI audit trail.
 */
@Repository
public interface AiLogMongoRepository extends MongoRepository<AiLogDocument, String> {

  /** Finds all logs for a specific candidate */
  List<AiLogDocument> findByCandidateIdOrderByCalledAtDesc(Long candidateId);

  /** Finds all logs for a specific operation type (CV_PARSING, MATCHING_SCORE, PREDICTION) */
  Page<AiLogDocument> findByOperationTypeOrderByCalledAtDesc(String operationType, Pageable pageable);

  /** Finds all failed AI calls */
  List<AiLogDocument> findByStatusOrderByCalledAtDesc(String status);

  /** Finds logs for a specific job offer */
  List<AiLogDocument> findByJobOfferIdOrderByCalledAtDesc(Long jobOfferId);

  /** Finds logs for a specific application */
  List<AiLogDocument> findByApplicationIdOrderByCalledAtDesc(Long applicationId);

  /** Finds logs by AI model version */
  List<AiLogDocument> findByModelVersion(String modelVersion);

  /** Finds slow AI calls above a duration threshold (in ms) */
  @Query("{ 'duration_ms': { $gte: ?0 } }")
  List<AiLogDocument> findSlowCalls(Long thresholdMs);

  /** Finds all logs within a time range */
  @Query("{ 'called_at': { $gte: ?0, $lte: ?1 } }")
  List<AiLogDocument> findBetweenDates(LocalDateTime from, LocalDateTime to);

  /** Counts failed calls for a specific operation type */
  long countByOperationTypeAndStatus(String operationType, String status);

  /** Finds logs triggered by a specific N8N workflow */
  List<AiLogDocument> findByWorkflowId(String workflowId);

  /** Finds the most recent log for a specific candidate and operation */
  AiLogDocument findTopByCandidateIdAndOperationTypeOrderByCalledAtDesc(
    Long candidateId, String operationType);
}
