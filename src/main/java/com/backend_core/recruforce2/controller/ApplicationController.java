package com.backend_core.recruforce2.controller;

import com.backend_core.recruforce2.domain.enums.ApplicationStatus;
import com.backend_core.recruforce2.dto.request.ApplicationRequest;
import com.backend_core.recruforce2.dto.response.ApplicationResponse;
import com.backend_core.recruforce2.service.ApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for application management.
 */
@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Applications", description = "Application submission and tracking endpoints")
public class ApplicationController {

  private final ApplicationService applicationService;

  @Operation(summary = "Submit a new application")
  @PostMapping
  public ResponseEntity<ApplicationResponse> submit(@Valid @RequestBody ApplicationRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
      .body(applicationService.submit(request));
  }

  @Operation(summary = "Change application status")
  @PatchMapping("/{id}/status")
  @PreAuthorize("hasAnyRole('RECRUITER', 'ADMIN')")
  public ResponseEntity<ApplicationResponse> changeStatus(
    @PathVariable Long id,
    @RequestParam ApplicationStatus status) {
    return ResponseEntity.ok(applicationService.changeStatus(id, status));
  }

  @Operation(summary = "Get application by ID")
  @GetMapping("/{id}")
  @PreAuthorize("hasAnyRole('RECRUITER', 'MANAGER', 'ADMIN')")
  public ResponseEntity<ApplicationResponse> getById(@PathVariable Long id) {
    return ResponseEntity.ok(applicationService.getById(id));
  }

  @Operation(summary = "Get all applications for a job offer")
  @GetMapping("/job-offer/{jobOfferId}")
  @PreAuthorize("hasAnyRole('RECRUITER', 'MANAGER', 'ADMIN')")
  public ResponseEntity<Page<ApplicationResponse>> getByJobOffer(
    @PathVariable Long jobOfferId,
    Pageable pageable) {
    return ResponseEntity.ok(applicationService.getByJobOffer(jobOfferId, pageable));
  }

  @Operation(summary = "Get qualified applications for a job offer")
  @GetMapping("/job-offer/{jobOfferId}/qualified")
  @PreAuthorize("hasAnyRole('RECRUITER', 'MANAGER', 'ADMIN')")
  public ResponseEntity<Page<ApplicationResponse>> getQualifiedByJobOffer(
    @PathVariable Long jobOfferId,
    Pageable pageable) {
    return ResponseEntity.ok(applicationService.getQualifiedByJobOffer(jobOfferId, pageable));
  }
}
