package com.backend_core.recruforce2.controller;

import com.backend_core.recruforce2.dto.request.JobOfferRequest;
import com.backend_core.recruforce2.dto.response.JobOfferResponse;
import com.backend_core.recruforce2.service.JobOfferService;
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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for job offer management.
 * Requires RECRUITER or ADMIN role.
 */
@RestController
@RequestMapping("/api/job-offers")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Job Offers", description = "Job offer CRUD and management endpoints")
public class JobOfferController {

  private final JobOfferService jobOfferService;

  @Operation(summary = "Create a new job offer")
  @PostMapping
  @PreAuthorize("hasAnyRole('RECRUITER', 'ADMIN')")
  public ResponseEntity<JobOfferResponse> create(
    @Valid @RequestBody JobOfferRequest request,
    Authentication authentication) {
    Long userId = getUserId(authentication);
    return ResponseEntity.status(HttpStatus.CREATED)
      .body(jobOfferService.create(request, userId));
  }

  @Operation(summary = "Update an existing job offer")
  @PutMapping("/{id}")
  @PreAuthorize("hasAnyRole('RECRUITER', 'ADMIN')")
  public ResponseEntity<JobOfferResponse> update(
    @PathVariable Long id,
    @Valid @RequestBody JobOfferRequest request) {
    return ResponseEntity.ok(jobOfferService.update(id, request));
  }

  @Operation(summary = "Publish a job offer")
  @PostMapping("/{id}/publish")
  @PreAuthorize("hasAnyRole('RECRUITER', 'ADMIN')")
  public ResponseEntity<Void> publish(@PathVariable Long id) {
    jobOfferService.publish(id);
    return ResponseEntity.ok().build();
  }

  @Operation(summary = "Archive a job offer")
  @PostMapping("/{id}/archive")
  @PreAuthorize("hasAnyRole('RECRUITER', 'ADMIN')")
  public ResponseEntity<Void> archive(@PathVariable Long id) {
    jobOfferService.archive(id);
    return ResponseEntity.ok().build();
  }

  @Operation(summary = "Get job offer by ID")
  @GetMapping("/{id}")
  public ResponseEntity<JobOfferResponse> getById(@PathVariable Long id) {
    return ResponseEntity.ok(jobOfferService.getById(id));
  }

  @Operation(summary = "Get all job offers (paginated)")
  @GetMapping
  public ResponseEntity<Page<JobOfferResponse>> getAll(Pageable pageable) {
    return ResponseEntity.ok(jobOfferService.getAll(pageable));
  }

  @Operation(summary = "Search job offers by keyword")
  @GetMapping("/search")
  public ResponseEntity<Page<JobOfferResponse>> search(
    @RequestParam String keyword,
    Pageable pageable) {
    return ResponseEntity.ok(jobOfferService.searchByKeyword(keyword, pageable));
  }

  private Long getUserId(Authentication authentication) {
    return ((com.backend_core.recruforce2.domain.entities.User) authentication.getPrincipal()).getId();
  }
}
