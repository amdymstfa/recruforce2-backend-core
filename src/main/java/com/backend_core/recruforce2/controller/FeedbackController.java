package com.backend_core.recruforce2.controller;

import com.backend_core.recruforce2.dto.request.FeedbackRequest;
import com.backend_core.recruforce2.dto.response.FeedbackResponse;
import com.backend_core.recruforce2.service.FeedbackService;
import com.backend_core.recruforce2.domain.entities.User;
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

import java.util.List;

@RestController
@RequestMapping("/api/feedbacks")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Feedbacks", description = "Interview feedback management")
public class FeedbackController {

  private final FeedbackService feedbackService;

  @Operation(summary = "Submit feedback for an interview")
  @PostMapping
  @PreAuthorize("hasAnyRole('ADMIN', 'RECRUITER', 'MANAGER')")
  public ResponseEntity<FeedbackResponse> create(@Valid @RequestBody FeedbackRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
      .body(feedbackService.create(request));
  }

  @Operation(summary = "Get feedback by interview ID")
  @GetMapping("/interview/{interviewId}")
  @PreAuthorize("hasAnyRole('ADMIN', 'RECRUITER', 'MANAGER')")
  public ResponseEntity<FeedbackResponse> getByInterview(@PathVariable Long interviewId, Authentication authentication) {
    return ResponseEntity.ok(feedbackService.getByInterviewId(interviewId));
  }

  @Operation(summary = "Get all feedbacks (admin)")
  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<List<FeedbackResponse>> getAll() {
    return ResponseEntity.ok(feedbackService.getAll());
  }
}
