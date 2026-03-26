package com.backend_core.recruforce2.controller;

import com.backend_core.recruforce2.dto.request.CriteriaRequest;
import com.backend_core.recruforce2.dto.response.CriteriaResponse;
import com.backend_core.recruforce2.service.CriteriaService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/criteria")
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Criteria", description = "Interview evaluation criteria management")
@RequiredArgsConstructor
public class CriteriaController {

  private final CriteriaService criteriaService;

  @GetMapping
  public ResponseEntity<List<CriteriaResponse>> getAll() {
    return ResponseEntity.ok(criteriaService.getAll());
  }

  @PostMapping
  public ResponseEntity<CriteriaResponse> create(@RequestBody CriteriaRequest request) {
    return ResponseEntity.ok(criteriaService.create(request));
  }

  @PutMapping("/{id}")
  public ResponseEntity<CriteriaResponse> update(
    @PathVariable Long id, @RequestBody CriteriaRequest request) {
    return ResponseEntity.ok(criteriaService.update(id, request));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    criteriaService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
