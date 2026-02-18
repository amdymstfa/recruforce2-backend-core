package com.backend_core.recruforce2.domain.entities;

import com.backend_core.recruforce2.domain.enums.NotificationType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Represents an in-app or email notification sent to a user.
 */
@Entity
@Table(name = "notifications", indexes = {
  @Index(name = "idx_notifications_recipient_id", columnList = "recipient_id"),
  @Index(name = "idx_notifications_is_read", columnList = "is_read"),
  @Index(name = "idx_notifications_created_at", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /** The user who receives this notification */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "recipient_id", nullable = false)
  private User recipient;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 30)
  private NotificationType type;

  @NotBlank(message = "Notification title is required")
  @Column(nullable = false, length = 200)
  private String title;

  @Column(columnDefinition = "TEXT")
  private String message;

  /** Optional deep link to the related resource */
  @Column(length = 500)
  private String link;

  @Column(name = "is_read", nullable = false)
  @Builder.Default
  private Boolean isRead = false;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "read_at")
  private LocalDateTime readAt;

  // -------------------------------------------------------
  // Lifecycle hooks
  // -------------------------------------------------------

  @PrePersist
  protected void onCreate() {
    this.createdAt = LocalDateTime.now();
  }

  // -------------------------------------------------------
  // Business methods
  // -------------------------------------------------------

  /** Creates a new notification for a recipient */
  public static Notification create(User recipient, NotificationType type,
                                    String title, String message, String link) {
    return Notification.builder()
      .recipient(recipient)
      .type(type)
      .title(title)
      .message(message)
      .link(link)
      .build();
  }

  /** Marks this notification as read */
  public void markAsRead() {
    this.isRead = true;
    this.readAt = LocalDateTime.now();
  }

  /** Deletes this notification (soft delete via service layer) */
  public void delete() {
    this.isRead = true;
  }
}
