package com.backend_core.recruforce2.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Records all significant actions performed in the platform for audit and traceability.
 * Immutable once created — no updates allowed.
 */
@Entity
@Table(name = "audit_logs", indexes = {
  @Index(name = "idx_audit_user_id", columnList = "user_id"),
  @Index(name = "idx_audit_entity", columnList = "entity_type, entity_id"),
  @Index(name = "idx_audit_timestamp", columnList = "timestamp")
})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /** The user who performed the action */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  /** The action performed (e.g. CREATE, UPDATE, DELETE, LOGIN, EXPORT) */
  @Column(nullable = false, length = 50)
  private String action;

  /** The type of entity affected (e.g. JobOffer, Application, Interview) */
  @Column(name = "entity_type", nullable = false, length = 100)
  private String entityType;

  /** The ID of the affected entity */
  @Column(name = "entity_id")
  private Long entityId;

  /** Human-readable description of the change */
  @Column(columnDefinition = "TEXT")
  private String details;

  /** IP address of the client that performed the action */
  @Column(name = "ip_address", length = 45)
  private String ipAddress;

  /** Timestamp when the action occurred */
  @Column(nullable = false, updatable = false)
  private LocalDateTime timestamp;

  // -------------------------------------------------------
  // Lifecycle hooks
  // -------------------------------------------------------

  @PrePersist
  protected void onCreate() {
    this.timestamp = LocalDateTime.now();
  }

  // -------------------------------------------------------
  // Business methods
  // -------------------------------------------------------

  /**
   * Factory method — creates an audit log entry.
   *
   * @param user       the user performing the action
   * @param action     the action label
   * @param entityType the type of entity affected
   * @param entityId   the ID of the entity affected
   * @param details    a human-readable description
   * @param ipAddress  the client IP address
   * @return a new AuditLog instance
   */
  public static AuditLog record(User user,
                                String action,
                                String entityType,
                                Long entityId,
                                String details,
                                String ipAddress) {
    return AuditLog.builder()
      .user(user)
      .action(action)
      .entityType(entityType)
      .entityId(entityId)
      .details(details)
      .ipAddress(ipAddress)
      .build();
  }

  /** Returns a readable summary of this audit entry */
  public String view() {
    return String.format("[%s] %s performed '%s' on %s#%d — %s",
      timestamp, user.getFullName(), action, entityType, entityId, details);
  }
}
