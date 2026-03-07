package com.backend_core.recruforce2.controller;

import com.backend_core.recruforce2.dto.response.PredictionResponse;
import com.backend_core.recruforce2.service.PredictionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for AI prediction management.
 */
@RestController
@RequestMapping("/api/predictions")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Predictions", description = "AI prediction endpoints")
public class PredictionController {

  private final PredictionService predictionService;

  @Operation(summary = "Generate AI prediction for candidate/job offer pair")
  @PostMapping
  @PreAuthorize("hasAnyRole('RECRUITER', 'ADMIN')")
  public ResponseEntity<PredictionResponse> predict(
    @RequestParam Long candidateId,
    @RequestParam Long jobOfferId) {
    return ResponseEntity.ok(predictionService.predict(candidateId, jobOfferId));
  }

  @Operation(summary = "Get prediction by ID")
  @GetMapping("/{id}")
  @PreAuthorize("hasAnyRole('RECRUITER', 'MANAGER', 'ADMIN')")
  public ResponseEntity<PredictionResponse> getById(@PathVariable Long id) {
    return ResponseEntity.ok(predictionService.getById(id));
  }

  @Operation(summary = "Get latest prediction for candidate/job offer pair")
  @GetMapping("/latest")
  @PreAuthorize("hasAnyRole('RECRUITER', 'MANAGER', 'ADMIN')")
  public ResponseEntity<PredictionResponse> getLatest(
    @RequestParam Long candidateId,
    @RequestParam Long jobOfferId) {
    PredictionResponse prediction = predictionService.getLatestForCandidateAndJobOffer(
      candidateId, jobOfferId);
    return prediction != null ? ResponseEntity.ok(prediction) : ResponseEntity.notFound().build();
  }
}
