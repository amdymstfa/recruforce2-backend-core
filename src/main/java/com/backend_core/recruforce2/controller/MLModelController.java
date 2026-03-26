package com.backend_core.recruforce2.controller;

import com.backend_core.recruforce2.dto.response.MLModelResponse;
import com.backend_core.recruforce2.service.MLModelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ml-models")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "ML Models", description = "Machine learning model management endpoints")
public class MLModelController {

  private final MLModelService mlModelService;

  @Operation(summary = "Get active ML model")
  @GetMapping("/active")
  @PreAuthorize("hasAnyRole('RECRUITER', 'MANAGER', 'ADMIN')")
  public ResponseEntity<MLModelResponse> getActive() {
    return ResponseEntity.ok(mlModelService.getActiveModel());
  }

  @Operation(summary = "Get all ML models")
  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<List<MLModelResponse>> getAll() {
    return ResponseEntity.ok(mlModelService.getAllModels());
  }

  @PatchMapping("/{id}/activate")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> activateModel(@PathVariable Long id) {
    mlModelService.activate(id);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/retrain")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> triggerRetraining() {
    return ResponseEntity.accepted().build();
  }
}
