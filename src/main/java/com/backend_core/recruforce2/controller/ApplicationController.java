package com.backend_core.recruforce2.controller;

import com.backend_core.recruforce2.dto.request.ApplicationRequest;
import com.backend_core.recruforce2.dto.response.ApplicationResponse;
import com.backend_core.recruforce2.service.ApplicationService;
import com.backend_core.recruforce2.domain.enums.ApplicationStatus;
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

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Applications")
public class ApplicationController {

  private final ApplicationService applicationService;

  @PostMapping
  public ResponseEntity<ApplicationResponse> submit(@Valid @RequestBody ApplicationRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(applicationService.submit(request));
  }

  @PatchMapping("/{id}/score")
  @PreAuthorize("hasAnyRole('ADMIN', 'RECRUITER')")
  public ResponseEntity<Void> updateScore(@PathVariable Long id, @RequestParam Integer score) {
    applicationService.updateScore(id, score);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApplicationResponse> getById(@PathVariable Long id) {
    return ResponseEntity.ok(applicationService.getById(id));
  }

  @GetMapping("/job-offer/{jobOfferId}")
  public ResponseEntity<Page<ApplicationResponse>> getByJobOffer(@PathVariable Long jobOfferId, Pageable pageable) {
    return ResponseEntity.ok(applicationService.getByJobOffer(jobOfferId, pageable));
  }
}
