package com.backend_core.recruforce2.controller;

import com.backend_core.recruforce2.dto.request.CandidateProfileRequest;
import com.backend_core.recruforce2.dto.response.CandidateProfileResponse;
import com.backend_core.recruforce2.service.CandidateService;
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
import org.springframework.web.multipart.MultipartFile;

/**
 * REST controller for candidate profile management.
 */
@RestController
@RequestMapping("/api/candidates")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Candidates", description = "Candidate profile management endpoints")
public class CandidateController {

  private final CandidateService candidateService;

  @Operation(summary = "Create a new candidate profile")
  @PostMapping
  @PreAuthorize("hasAnyRole('RECRUITER', 'ADMIN', 'ROLE_ADMIN')")
  public ResponseEntity<CandidateProfileResponse> create(
    @Valid @RequestBody CandidateProfileRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
      .body(candidateService.create(request));
  }

  @Operation(summary = "Upload CV for a candidate and trigger workflow")
  @PostMapping("/{id}/cv")
  @PreAuthorize("hasAnyRole('RECRUITER', 'ADMIN', 'ROLE_ADMIN')")
  public ResponseEntity<Void> uploadCv(
    @PathVariable Long id,
    @RequestParam("file") MultipartFile file,
    @RequestParam(value = "parsedCvId", required = false) String parsedCvId,
    @RequestParam("jobOfferId") Long jobOfferId) { // <--- AJOUT DU PARAMÈTRE ICI

    candidateService.uploadCv(id, file, parsedCvId, jobOfferId);
    return ResponseEntity.ok().build();
  }

  @Operation(summary = "Get candidate by ID")
  @GetMapping("/{id}")
  @PreAuthorize("hasAnyRole('RECRUITER', 'MANAGER', 'ADMIN')")
  public ResponseEntity<CandidateProfileResponse> getById(@PathVariable Long id) {
    return ResponseEntity.ok(candidateService.getById(id));
  }

  @Operation(summary = "Search candidates by keyword")
  @GetMapping("/search")
  @PreAuthorize("hasAnyRole('RECRUITER', 'MANAGER', 'ADMIN')")
  public ResponseEntity<Page<CandidateProfileResponse>> search(
    @RequestParam String keyword,
    Pageable pageable) {
    return ResponseEntity.ok(candidateService.searchByKeyword(keyword, pageable));
  }

  @Operation(summary = "Delete candidate (GDPR erasure)")
  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    candidateService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
