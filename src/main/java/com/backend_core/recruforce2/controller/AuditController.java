package com.backend_core.recruforce2.controller;

import com.backend_core.recruforce2.dto.response.AuditLogResponse;
import com.backend_core.recruforce2.service.AuditService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/audit")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Audit", description = "Consulter les journaux d'activité")
@AllArgsConstructor
public class AuditController {
  private final AuditService auditService ;

  @GetMapping
  public ResponseEntity<Page<AuditLogResponse>> getLogs(Pageable pageable) {
    return ResponseEntity.ok(auditService.findAll(pageable));
  }

  @GetMapping("/user/{userId}")
  public ResponseEntity<List<AuditLogResponse>> getLogsByUser(@PathVariable Long userId) {
    return ResponseEntity.ok(auditService.findByUserId(userId));
  }
}
