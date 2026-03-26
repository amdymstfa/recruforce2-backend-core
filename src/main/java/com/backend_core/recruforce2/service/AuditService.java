package com.backend_core.recruforce2.service;

import com.backend_core.recruforce2.dto.response.AuditLogResponse;
import com.backend_core.recruforce2.repository.AuditLogRepository;
import com.backend_core.recruforce2.domain.entities.AuditLog;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuditService {

  private final AuditLogRepository auditLogRepository;

  @Transactional(readOnly = true)
  public Page<AuditLogResponse> findAll(Pageable pageable) {
    return auditLogRepository.findAll(pageable)
      .map(this::mapToResponse);
  }

  @Transactional(readOnly = true)
  public List<AuditLogResponse> findByUserId(Long userId) {
    return auditLogRepository.findByUserIdOrderByTimestampDesc(userId)
      .stream()
      .map(this::mapToResponse)
      .collect(Collectors.toList());
  }

  private AuditLogResponse mapToResponse(AuditLog log) {
    return AuditLogResponse.builder()
      .id(log.getId())
      .userId(log.getUser().getId())
      .userName(log.getUser().getFirstName() + " " + log.getUser().getLastName())
      .action(log.getAction())
      .entityType(log.getEntityType())
      .entityId(log.getEntityId())
      .details(log.getDetails())
      .ipAddress(log.getIpAddress())
      .timestamp(log.getTimestamp())
      .build();
  }
}
