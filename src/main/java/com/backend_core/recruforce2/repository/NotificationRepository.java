package com.backend_core.recruforce2.repository;

import com.backend_core.recruforce2.domain.entities.Notification;
import com.backend_core.recruforce2.domain.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * JPA repository for {@link Notification} entity.
 * Provides access to the {@code notifications} table in PostgreSQL.
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

  /** Finds all notifications for a specific user (paginated, most recent first) */
  Page<Notification> findByRecipientIdOrderByCreatedAtDesc(Long recipientId, Pageable pageable);

  /** Finds all unread notifications for a specific user */
  List<Notification> findByRecipientIdAndIsReadFalseOrderByCreatedAtDesc(Long recipientId);

  /** Finds notifications by type for a specific user */
  List<Notification> findByRecipientIdAndType(Long recipientId, NotificationType type);

  /** Counts unread notifications for a specific user */
  long countByRecipientIdAndIsReadFalse(Long recipientId);

  /** Marks all notifications as read for a specific user */
  @Modifying
  @Query("UPDATE Notification n SET n.isRead = true, n.readAt = CURRENT_TIMESTAMP " +
    "WHERE n.recipient.id = :recipientId AND n.isRead = false")
  int markAllAsReadForUser(@Param("recipientId") Long recipientId);

  /** Finds all unread notifications of a specific type (for alert processing) */
  List<Notification> findByTypeAndIsReadFalse(NotificationType type);

  /** Deletes all read notifications for a user (cleanup) */
  @Modifying
  @Query("DELETE FROM Notification n WHERE n.recipient.id = :recipientId AND n.isRead = true")
  int deleteReadNotificationsForUser(@Param("recipientId") Long recipientId);
}
