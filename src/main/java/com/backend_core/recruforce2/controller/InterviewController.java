package com.backend_core.recruforce2.controller;

import com.backend_core.recruforce2.dto.request.InterviewRequest;
import com.backend_core.recruforce2.dto.response.InterviewResponse;
import com.backend_core.recruforce2.service.InterviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST controller for interview management.
 */
@RestController
@RequestMapping("/api/interviews")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Interviews", description = "Interview scheduling and management endpoints")
public class InterviewController {

  private final InterviewService interviewService;

  @Operation(summary = "Schedule a new interview")
  @PostMapping
  @PreAuthorize("hasAnyRole('RECRUITER', 'MANAGER', 'ADMIN')")
  public ResponseEntity<InterviewResponse> schedule(@Valid @RequestBody InterviewRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
      .body(interviewService.schedule(request));
  }

  @Operation(summary = "Confirm interview (via invitation token)")
  @PostMapping("/confirm/{token}")
  public ResponseEntity<InterviewResponse> confirm(@PathVariable String token) {
    return ResponseEntity.ok(interviewService.confirm(token));
  }

  @Operation(summary = "Cancel an interview")
  @PostMapping("/{id}/cancel")
  @PreAuthorize("hasAnyRole('RECRUITER', 'MANAGER', 'ADMIN')")
  public ResponseEntity<Void> cancel(@PathVariable Long id) {
    interviewService.cancel(id);
    return ResponseEntity.ok().build();
  }

  @Operation(summary = "Reschedule an interview")
  @PatchMapping("/{id}/reschedule")
  @PreAuthorize("hasAnyRole('RECRUITER', 'MANAGER', 'ADMIN')")
  public ResponseEntity<InterviewResponse> reschedule(
    @PathVariable Long id,
    @RequestParam LocalDateTime newDateTime) {
    return ResponseEntity.ok(interviewService.reschedule(id, newDateTime));
  }

  @Operation(summary = "Get interview by ID")
  @GetMapping
  @PreAuthorize("hasAnyRole('RECRUITER', 'MANAGER', 'ADMIN')")
  public ResponseEntity<List<InterviewResponse>> getAll() {
    return ResponseEntity.ok(interviewService.getAll());
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAnyRole('RECRUITER', 'MANAGER', 'ADMIN')")
  public ResponseEntity<InterviewResponse> getById(@PathVariable Long id) {
    return ResponseEntity.ok(interviewService.getById(id));
  }

  @Operation(summary = "Get all interviews for an application")
  @GetMapping("/application/{applicationId}")
  @PreAuthorize("hasAnyRole('RECRUITER', 'MANAGER', 'ADMIN')")
  public ResponseEntity<List<InterviewResponse>> getByApplication(@PathVariable Long applicationId) {
    return ResponseEntity.ok(interviewService.getByApplication(applicationId));
  }

  @Operation(summary = "Get interviews based on role")
  @GetMapping("/upcoming")
  @PreAuthorize("hasAnyRole('RECRUITER', 'MANAGER', 'ADMIN')")
  public ResponseEntity<List<InterviewResponse>> getUpcoming(Authentication authentication) {
    com.backend_core.recruforce2.domain.entities.User user =
      (com.backend_core.recruforce2.domain.entities.User) authentication.getPrincipal();

    String role = user.getRole().name();

    if ("ADMIN".equals(role)) {
      return ResponseEntity.ok(interviewService.getAll());
    }

    return ResponseEntity.ok(interviewService.getUpcomingByInterviewer(user.getId()));
  }

  private Long getUserId(Authentication authentication) {
    return ((com.backend_core.recruforce2.domain.entities.User) authentication.getPrincipal()).getId();
  }
}
