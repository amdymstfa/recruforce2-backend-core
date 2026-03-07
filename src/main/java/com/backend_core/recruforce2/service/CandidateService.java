package com.backend_core.recruforce2.service;

import com.backend_core.recruforce2.domain.entities.Candidate;
import com.backend_core.recruforce2.dto.request.CandidateProfileRequest;
import com.backend_core.recruforce2.dto.response.CandidateProfileResponse;
import com.backend_core.recruforce2.mongo.document.ParsedCvDocument;
import com.backend_core.recruforce2.mongo.repository.ParsedCvMongoRepository;
import com.backend_core.recruforce2.repository.CandidateProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

/**
 * Service for managing candidate profiles.
 * Handles hybrid PostgreSQL + MongoDB operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CandidateService {

  private final CandidateProfileRepository candidateRepository;
  private final ParsedCvMongoRepository parsedCvMongoRepository;

  @Transactional
  public CandidateProfileResponse create(CandidateProfileRequest request) {
    if (candidateRepository.existsByEmail(request.getEmail())) {
      throw new IllegalArgumentException("Candidate already exists");
    }

    Candidate candidate = Candidate.builder()
      .firstName(request.getFirstName())
      .lastName(request.getLastName())
      .email(request.getEmail())
      .phone(request.getPhone())
      .address(request.getAddress())
      .birthDate(request.getBirthDate())
      .createdAt(LocalDateTime.now())
      .build();

    return mapToResponse(candidateRepository.save(candidate));
  }

  @Transactional
  public void uploadCv(Long candidateId, MultipartFile file, String parsedCvId) {
    Candidate candidate = candidateRepository.findById(candidateId)
      .orElseThrow(() -> new IllegalArgumentException("Candidate not found"));

    // TODO: Save file to storage and get path
    String cvPath = "/uploads/cvs/" + file.getOriginalFilename();

    candidate.attachCv(cvPath, parsedCvId);
    candidateRepository.save(candidate);
    log.info("CV uploaded for candidate: {}", candidateId);
  }

  @Transactional(readOnly = true)
  public CandidateProfileResponse getById(Long id) {
    Candidate candidate = candidateRepository.findById(id)
      .orElseThrow(() -> new IllegalArgumentException("Candidate not found"));
    return mapToResponse(candidate);
  }

  @Transactional(readOnly = true)
  public Page<CandidateProfileResponse> searchByKeyword(String keyword, Pageable pageable) {
    return candidateRepository.searchByKeyword(keyword, pageable).map(this::mapToResponse);
  }

  @Transactional
  public void delete(Long id) {
    Candidate candidate = candidateRepository.findById(id)
      .orElseThrow(() -> new IllegalArgumentException("Candidate not found"));

    // GDPR erasure
    if (candidate.getParsedCvId() != null) {
      parsedCvMongoRepository.deleteByCandidateId(id);
    }

    candidate.deleteGDPR();
    candidateRepository.save(candidate);
  }

  private CandidateProfileResponse mapToResponse(Candidate candidate) {
    return CandidateProfileResponse.builder()
      .id(candidate.getId())
      .firstName(candidate.getFirstName())
      .lastName(candidate.getLastName())
      .email(candidate.getEmail())
      .phone(candidate.getPhone())
      .address(candidate.getAddress())
      .birthDate(candidate.getBirthDate())
      .cvPath(candidate.getCvPath())
      .parsedCvId(candidate.getParsedCvId())
      .applicationsCount(candidate.getApplications() != null ? candidate.getApplications().size() : 0)
      .createdAt(candidate.getCreatedAt())
      .build();
  }
}
