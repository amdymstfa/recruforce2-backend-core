package com.backend_core.recruforce2.service;

import com.backend_core.recruforce2.domain.entities.Candidate;
import com.backend_core.recruforce2.domain.entities.CandidateSkill;
import com.backend_core.recruforce2.domain.entities.Experience;
import com.backend_core.recruforce2.domain.entities.Skill;
import com.backend_core.recruforce2.domain.enums.SkillLevel;
import com.backend_core.recruforce2.dto.request.CandidateProfileRequest;
import com.backend_core.recruforce2.dto.request.ExperienceRequest;
import com.backend_core.recruforce2.dto.response.CandidateProfileResponse;
import com.backend_core.recruforce2.mongo.document.ParsedCvDocument;
import com.backend_core.recruforce2.mongo.repository.ParsedCvMongoRepository;
import com.backend_core.recruforce2.repository.CandidateProfileRepository;
import com.backend_core.recruforce2.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;

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
  private final SkillRepository skillRepository;

  @Transactional
  public CandidateProfileResponse create(CandidateProfileRequest request) {
    // 1. Sécurité : vérifier si le candidat existe déjà
    if (candidateRepository.existsByEmail(request.getEmail())) {
      throw new IllegalArgumentException("Candidate already exists with email: " + request.getEmail());
    }

    // 2. Création de l'entité Candidate
    Candidate candidate = Candidate.builder()
      .firstName(request.getFirstName())
      .lastName(request.getLastName())
      .email(request.getEmail())
      .phone(request.getPhone())
      .address(request.getAddress())
      .parsedCvId(request.getParsedCvId())
      .createdAt(LocalDateTime.now())
      .skills(new ArrayList<>())      // Initialisation pour éviter NullPointerException
      .experiences(new ArrayList<>()) // Initialisation pour éviter NullPointerException
      .build();

    // 3. Sauvegarde des compétences (Le point qui bloque)
    if (request.getSkills() != null) {
      request.getSkills().forEach(name -> {
        // IMPORTANT : On cherche la compétence, sinon on la crée avec TOUS les champs obligatoires
        Skill skillRef = skillRepository.findByNameIgnoreCase(name)
          .orElseGet(() -> skillRepository.save(
            Skill.builder()
              .name(name.toUpperCase())
              .type(com.backend_core.recruforce2.domain.enums.SkillType.TECHNICAL) // <--- OBLIGATOIRE
              .requiredLevel(com.backend_core.recruforce2.domain.enums.SkillLevel.INTERMEDIATE) // <--- OBLIGATOIRE
              .build()
          ));

        candidate.getSkills().add(CandidateSkill.builder()
          .candidate(candidate)
          .skill(skillRef)
          .masteryLevel(com.backend_core.recruforce2.domain.enums.SkillLevel.INTERMEDIATE)
          .build());
      });
    }

    // 4. Sauvegarde des expériences
    if (request.getExperiences() != null) {
      for (ExperienceRequest expReq : request.getExperiences()) {
        candidate.getExperiences().add(Experience.builder()
          .position(expReq.getPosition())
          .company(expReq.getCompany())
          .description(expReq.getDescription())
          .candidate(candidate)
          .build());
      }
    }

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
