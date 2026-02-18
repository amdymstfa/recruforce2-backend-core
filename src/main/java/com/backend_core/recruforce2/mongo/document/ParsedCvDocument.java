package com.backend_core.recruforce2.mongo.document;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * MongoDB document storing the structured result of an AI-parsed CV.
 * <p>
 * Stored in the {@code parsed_cvs} collection.
 * Linked to the PostgreSQL {@code candidates} table via {@code candidateId}.
 * <p>
 * This document is populated by the Python AI microservice (recruforce2-ai-model)
 * via the N8N workflow after a CV is uploaded or received by email.
 */
@Document(collection = "parsed_cvs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParsedCvDocument {

  /** MongoDB document ID — stored in Candidate.parsedCvId (PostgreSQL) */
  @Id
  private String id;

  /** Reference to the PostgreSQL candidate ID */
  @Indexed
  @Field("candidate_id")
  private Long candidateId;

  /** Original filename of the uploaded CV */
  @Field("original_filename")
  private String originalFilename;

  /** MIME type of the uploaded file (application/pdf or application/vnd.openxmlformats...) */
  @Field("file_type")
  private String fileType;

  // -------------------------------------------------------
  // Extracted candidate identity
  // -------------------------------------------------------

  @Field("first_name")
  private String firstName;

  @Field("last_name")
  private String lastName;

  @Field("email")
  private String email;

  @Field("phone")
  private String phone;

  @Field("address")
  private String address;

  // -------------------------------------------------------
  // Extracted professional experiences
  // Each entry: { position, company, description, startDate, endDate, isCurrent }
  // -------------------------------------------------------

  @Field("experiences")
  private List<Map<String, Object>> experiences;

  // -------------------------------------------------------
  // Extracted education entries
  // Each entry: { degree, institution, field, startDate, endDate }
  // -------------------------------------------------------

  @Field("educations")
  private List<Map<String, Object>> educations;

  // -------------------------------------------------------
  // Extracted technical and soft skills
  // Each entry: { name, type, masteryLevel, yearsExperience }
  // -------------------------------------------------------

  @Field("skills")
  private List<Map<String, Object>> skills;

  // -------------------------------------------------------
  // Extracted languages
  // Each entry: { name, level }
  // -------------------------------------------------------

  @Field("languages")
  private List<Map<String, Object>> languages;

  // -------------------------------------------------------
  // Raw extracted text (before NLP structuring)
  // -------------------------------------------------------

  @Field("raw_text")
  private String rawText;

  // -------------------------------------------------------
  // AI parsing metadata
  // -------------------------------------------------------

  /** Confidence score of the AI parsing (0.0 to 1.0) */
  @Field("parsing_confidence")
  private Double parsingConfidence;

  /** NLP model version used for parsing */
  @Field("model_version")
  private String modelVersion;

  /** Name of the N8N workflow that triggered this parsing */
  @Field("workflow_id")
  private String workflowId;

  /** Timestamp when this document was created */
  @CreatedDate
  @Field("parsed_at")
  private LocalDateTime parsedAt;
}
