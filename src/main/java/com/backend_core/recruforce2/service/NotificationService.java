package com.backend_core.recruforce2.service;

import com.backend_core.recruforce2.domain.entities.Application;
import com.backend_core.recruforce2.domain.entities.Interview;
import com.backend_core.recruforce2.domain.entities.Notification;
import com.backend_core.recruforce2.domain.entities.User;
import com.backend_core.recruforce2.domain.enums.NotificationType;
import com.backend_core.recruforce2.dto.response.NotificationResponse;
import com.backend_core.recruforce2.repository.NotificationRepository;
import com.backend_core.recruforce2.repository.NotificationPreferencesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing notifications.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

  private final NotificationRepository notificationRepository;
  private final NotificationPreferencesRepository notificationPreferencesRepository;
  private final EmailService emailService;

  /**
   * Creates and sends a notification to a user.
   */
  @Transactional
  public NotificationResponse create(User recipient, NotificationType type,
                                     String title, String message, String link) {
    Notification notification = Notification.create(recipient, type, title, message, link);
    notification = notificationRepository.save(notification);

    // Check if user wants email notifications for this type
    notificationPreferencesRepository.findByUserId(recipient.getId())
      .ifPresent(prefs -> {
        boolean sendEmail = switch (type) {
          case NEW_APPLICATION -> prefs.getEmailNewCandidate();
          case INTERVIEW_SCHEDULED, INTERVIEW_REMINDER -> prefs.getEmailInterview();
          case DEADLINE_APPROACHING -> prefs.getEmailDeadline();
          default -> false;
        };

        if (sendEmail) {
          emailService.sendNotificationEmail(recipient, title, message);
        }
      });

    return mapToResponse(notification);
  }

  /**
   * Notifies a recruiter about a new application.
   */
  @Transactional
  public void notifyNewApplication(User recruiter, Application application) {
    String title = "New Application Received";
    String message = String.format(
      "A new application has been received from %s for the position: %s",
      application.getCandidate().getFullName(),
      application.getJobOffer().getTitle()
    );
    String link = "/applications/" + application.getId();

    create(recruiter, NotificationType.NEW_APPLICATION, title, message, link);
  }

  /**
   * Notifies an interviewer about a scheduled interview.
   */
  @Transactional
  public void notifyInterviewScheduled(User interviewer, Interview interview) {
    String title = "Interview Scheduled";
    String message = String.format(
      "An interview has been scheduled with %s on %s",
      interview.getApplication().getCandidate().getFullName(),
      interview.getDateTime()
    );
    String link = "/interviews/" + interview.getId();

    create(interviewer, NotificationType.INTERVIEW_SCHEDULED, title, message, link);
  }

  /**
   * Marks a notification as read.
   */
  @Transactional
  public void markAsRead(Long id) {
    Notification notification = notificationRepository.findById(id)
      .orElseThrow(() -> new IllegalArgumentException("Notification not found"));

    notification.markAsRead();
    notificationRepository.save(notification);
  }

  /**
   * Marks all notifications as read for a user.
   */
  @Transactional
  public void markAllAsReadForUser(Long userId) {
    int count = notificationRepository.markAllAsReadForUser(userId);
    log.info("Marked {} notifications as read for user {}", count, userId);
  }

  @Transactional(readOnly = true)
  public Page<NotificationResponse> getByRecipient(Long recipientId, Pageable pageable) {
    return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(recipientId, pageable)
      .map(this::mapToResponse);
  }

  @Transactional(readOnly = true)
  public List<NotificationResponse> getUnreadByRecipient(Long recipientId) {
    return notificationRepository.findByRecipientIdAndIsReadFalseOrderByCreatedAtDesc(recipientId)
      .stream()
      .map(this::mapToResponse)
      .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public long countUnread(Long recipientId) {
    return notificationRepository.countByRecipientIdAndIsReadFalse(recipientId);
  }

  private NotificationResponse mapToResponse(Notification notification) {
    return NotificationResponse.builder()
      .id(notification.getId())
      .type(notification.getType())
      .title(notification.getTitle())
      .message(notification.getMessage())
      .link(notification.getLink())
      .isRead(notification.getIsRead())
      .createdAt(notification.getCreatedAt())
      .readAt(notification.getReadAt())
      .build();
  }
}
