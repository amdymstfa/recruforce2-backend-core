package com.backend_core.recruforce2.controller;

import com.backend_core.recruforce2.dto.response.NotificationResponse;
import com.backend_core.recruforce2.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for notification management.
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Notifications", description = "Notification management endpoints")
public class NotificationController {

  private final NotificationService notificationService;

  @Operation(summary = "Get all notifications for current user")
  @GetMapping
  public ResponseEntity<Page<NotificationResponse>> getAll(
    Authentication authentication,
    Pageable pageable) {
    Long userId = getUserId(authentication);
    return ResponseEntity.ok(notificationService.getByRecipient(userId, pageable));
  }

  @Operation(summary = "Get unread notifications for current user")
  @GetMapping("/unread")
  public ResponseEntity<List<NotificationResponse>> getUnread(Authentication authentication) {
    Long userId = getUserId(authentication);
    return ResponseEntity.ok(notificationService.getUnreadByRecipient(userId));
  }

  @Operation(summary = "Get unread notification count")
  @GetMapping("/unread/count")
  public ResponseEntity<Long> getUnreadCount(Authentication authentication) {
    Long userId = getUserId(authentication);
    return ResponseEntity.ok(notificationService.countUnread(userId));
  }

  @Operation(summary = "Mark notification as read")
  @PatchMapping("/{id}/read")
  public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
    notificationService.markAsRead(id);
    return ResponseEntity.ok().build();
  }

  @Operation(summary = "Mark all notifications as read")
  @PatchMapping("/read-all")
  public ResponseEntity<Void> markAllAsRead(Authentication authentication) {
    Long userId = getUserId(authentication);
    notificationService.markAllAsReadForUser(userId);
    return ResponseEntity.ok().build();
  }

  private Long getUserId(Authentication authentication) {
    return ((com.backend_core.recruforce2.domain.entities.User) authentication.getPrincipal()).getId();
  }
}
