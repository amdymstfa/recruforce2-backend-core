package com.backend_core.recruforce2.mongo.document;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * MongoDB document logging all calls made to the Python AI microservice.
 * <p>
 * Stored in the {@code ai_logs} collection.
 * Used for debugging, auditing, and monitoring AI service performance.
 * <p>
 * Every call to the AI microservice (CV parsing, matching score, prediction)
 * generates one AiLogDocument entry.
 */
@Document(collection = "ai_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiLogDocument {

  /** MongoDB document ID */
  @Id
  private String id;

  // -------------------------------------------------------
  // Request context
  // -------------------------------------------------------

  /**
   * Type of AI operation performed.
   * Values: CV_PARSING, MATCHING_SCORE, PREDICTION
   */
  @Indexed
  @Field("operation_type")
  private String operationType;

  /** Reference to the PostgreSQL candidate ID (if applicable) */
  @Indexed
  @Field("candidate_id")
  private Long candidateId;

  /** Reference to the PostgreSQL job offer ID (if applicable) */
  @Field("job_offer_id")
  private Long jobOfferId;

  /** Reference to the PostgreSQL application ID (if applicable) */
  @Field("application_id")
  private Long applicationId;

  // -------------------------------------------------------
  // Request payload (sent to AI microservice)
  // -------------------------------------------------------

  /** JSON payload sent to the Python AI microservice */
  @Field("request_payload")
  private Map<String, Object> requestPayload;

  // -------------------------------------------------------
  // Response data (received from AI microservice)
  // -------------------------------------------------------

  /** JSON response received from the Python AI microservice */
  @Field("response_payload")
  private Map<String, Object> responsePayload;

  /**
   * HTTP status code returned by the AI microservice.
   * 200 = success, 4xx/5xx = error
   */
  @Field("http_status")
  private Integer httpStatus;

  /**
   * Outcome of the AI call.
   * Values: SUCCESS, FAILURE, TIMEOUT, ERROR
   */
  @Indexed
  @Field("status")
  private String status;

  /** Error message if the call failed */
  @Field("error_message")
  private String errorMessage;

  // -------------------------------------------------------
  // Performance metrics
  // -------------------------------------------------------

  /** Time taken to complete the AI call in milliseconds */
  @Field("duration_ms")
  private Long durationMs;

  /** Version of the AI model used */
  @Field("model_version")
  private String modelVersion;

  /** Name of the AI microservice endpoint called */
  @Field("endpoint")
  private String endpoint;

  // -------------------------------------------------------
  // Metadata
  // -------------------------------------------------------

  /** Timestamp when the AI call was initiated */
  @CreatedDate
  @Indexed
  @Field("called_at")
  private LocalDateTime calledAt;

  /** N8N workflow ID that triggered this AI call (if applicable) */
  @Field("workflow_id")
  private String workflowId;
}
