package com.backend_core.recruforce2.service;

import com.backend_core.recruforce2.domain.entities.*;
import com.backend_core.recruforce2.domain.enums.*;
import com.backend_core.recruforce2.dto.request.*;
import com.backend_core.recruforce2.dto.response.*;
import com.backend_core.recruforce2.exception.ResourceNotFoundException;
import com.backend_core.recruforce2.mongo.repository.ParsedCvMongoRepository;
import com.backend_core.recruforce2.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CandidateService {
  private final CandidateProfileRepository candidateRepository;
  private final ParsedCvMongoRepository parsedCvMongoRepository;
  private final SkillRepository skillRepository;
  private final RestTemplate restTemplate = new RestTemplate();

  @Value("${n8n.cv-webhook:http://recruforce2-n8n:5678/webhook/cv-parsing}")
  private String n8nCvWebhookUrl;

  /**
   * Crée ou met à jour le profil d'un candidat à partir des données structurées.
   */
  @Transactional
  public CandidateProfileResponse create(CandidateProfileRequest request) {
    Candidate candidate = candidateRepository.findByEmail(request.getEmail())
      .orElseGet(() -> Candidate.builder().email(request.getEmail()).createdAt(LocalDateTime.now()).build());

    candidate.setFirstName(request.getFirstName());
    candidate.setLastName(request.getLastName());
    candidate.setPhone(request.getPhone());
    candidate.setAddress(clean(request.getAddress()));
    candidate.setParsedCvId(request.getParsedCvId());

    // Gestion des Skills
    if (candidate.getSkills() == null) candidate.setSkills(new ArrayList<>());
    candidate.getSkills().clear();
    if (request.getSkills() != null) {
      request.getSkills().forEach(name -> {
        Skill s = skillRepository.findByNameIgnoreCase(name).orElseGet(() -> skillRepository.save(
          Skill.builder().name(name.toUpperCase()).type(SkillType.TECHNICAL).requiredLevel(SkillLevel.INTERMEDIATE).build()));
        candidate.getSkills().add(CandidateSkill.builder().candidate(candidate).skill(s).masteryLevel(SkillLevel.INTERMEDIATE).build());
      });
    }

    // Gestion des Expériences
    if (candidate.getExperiences() == null) candidate.setExperiences(new ArrayList<>());
    candidate.getExperiences().clear();
    if (request.getExperiences() != null) {
      request.getExperiences().forEach(req -> {
        candidate.getExperiences().add(Experience.builder()
          .position(clean(req.getPosition())).company(clean(req.getCompany())).description(clean(req.getDescription()))
          .startDate(parseDate(req.getStartDate())).endDate(parseDate(req.getEndDate()))
          .isCurrent(req.getIsCurrent() != null && req.getIsCurrent()).candidate(candidate).build());
      });
    }

    // Gestion de l'Éducation
    if (candidate.getEducations() == null) candidate.setEducations(new ArrayList<>());
    candidate.getEducations().clear();
    if (request.getEducations() != null) {
      request.getEducations().forEach(req -> {
        candidate.getEducations().add(Education.builder()
          .degree(clean(req.getDegree())).institution(clean(req.getInstitution())).field(clean(req.getField()))
          .startDate(parseDate(req.getStartDate())).endDate(parseDate(req.getEndDate())).candidate(candidate).build());
      });
    }

    // Gestion des Langues
    if (candidate.getLanguages() == null) candidate.setLanguages(new ArrayList<>());
    candidate.getLanguages().clear();
    if (request.getLanguages() != null) {
      request.getLanguages().forEach(req -> {
        LanguageLevel lvl = LanguageLevel.INTERMEDIATE;
        try { if(req.getLevel() != null) lvl = LanguageLevel.valueOf(req.getLevel().toUpperCase()); } catch(Exception e) {}
        candidate.getLanguages().add(Language.builder().name(clean(req.getName())).level(lvl).candidate(candidate).build());
      });
    }

    return mapToResponse(candidateRepository.save(candidate));
  }

  /**
   * Upload du CV et déclenchement du workflow n8n.
   */
  @Transactional
  public void uploadCv(Long id, MultipartFile file, String parsedId, Long jobOfferId) {
    Candidate c = candidateRepository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Candidate not found with id: " + id));

    // On attache le fichier (simulé par le chemin)
    c.attachCv("/uploads/" + file.getOriginalFilename(), parsedId);
    candidateRepository.save(c);

    // Préparation du payload pour n8n
    Map<String, Object> payload = new HashMap<>();
    payload.put("candidateId", c.getId());
    payload.put("candidateEmail", c.getEmail());
    payload.put("candidateName", c.getFullName());

    // Crucial : On passe l'ID de l'offre pour que n8n puisse créer la candidature finale
    payload.put("jobOfferId", jobOfferId);

    log.info("Sending trigger to n8n for candidate {} on job {}", c.getId(), jobOfferId);

    try {
      restTemplate.postForEntity(n8nCvWebhookUrl, payload, String.class);
    } catch (Exception e) {
      log.error("Failed to call n8n webhook: {}", e.getMessage());
    }
  }

  @Transactional(readOnly = true)
  public CandidateProfileResponse getById(Long id) {
    return candidateRepository.findById(id).map(this::mapToResponse)
      .orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));
  }

  @Transactional(readOnly = true)
  public Page<CandidateProfileResponse> searchByKeyword(String kw, Pageable p) {
    return candidateRepository.searchByKeyword(kw, p).map(this::mapToResponse);
  }

  @Transactional
  public void delete(Long id) {
    Candidate c = candidateRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not found"));
    if (c.getParsedCvId() != null) parsedCvMongoRepository.deleteByCandidateId(id);
    c.deleteGDPR();
    candidateRepository.save(c);
  }

  // --- Helpers ---

  private String clean(String s) {
    return s == null ? "" : s.replace("Ὄ", "").replace("Ἴ", "").trim();
  }

  private LocalDate parseDate(String d) {
    if (d == null || d.isBlank() || d.equalsIgnoreCase("null")) return null;
    try { return LocalDate.parse(d); } catch (DateTimeParseException e) { return null; }
  }

  private CandidateProfileResponse mapToResponse(Candidate c) {
    List<ExperienceResponse> ex = (c.getExperiences() == null) ? Collections.emptyList() :
      c.getExperiences().stream().map(e -> ExperienceResponse.builder()
        .id(e.getId()).position(e.getPosition()).company(e.getCompany()).description(e.getDescription())
        .startDate(e.getStartDate() != null ? e.getStartDate().toString() : null)
        .endDate(e.getEndDate() != null ? e.getEndDate().toString() : null)
        .isCurrent(e.getIsCurrent()).build()).collect(Collectors.toList());

    List<EducationResponse> ed = (c.getEducations() == null) ? Collections.emptyList() :
      c.getEducations().stream().map(e -> EducationResponse.builder()
        .id(e.getId()).degree(e.getDegree()).institution(e.getInstitution())
        .startDate(e.getStartDate() != null ? e.getStartDate().toString() : null)
        .endDate(e.getEndDate() != null ? e.getEndDate().toString() : null).build()).collect(Collectors.toList());

    List<LanguageResponse> lg = (c.getLanguages() == null) ? Collections.emptyList() :
      c.getLanguages().stream().map(l -> LanguageResponse.builder()
        .id(l.getId()).name(l.getName()).level(l.getLevel() != null ? l.getLevel().name() : "INTERMEDIATE")
        .build()).collect(Collectors.toList());

    List<CandidateSkillResponse> sk = (c.getSkills() == null) ? Collections.emptyList() :
      c.getSkills().stream().map(s -> CandidateSkillResponse.builder()
        .id(s.getId()).name(s.getSkill().getName()).type(s.getSkill().getType().name())
        .masteryLevel(s.getMasteryLevel().name()).build()).collect(Collectors.toList());

    return CandidateProfileResponse.builder()
      .id(c.getId())
      .firstName(c.getFirstName())
      .lastName(c.getLastName())
      .email(c.getEmail())
      .phone(c.getPhone())
      .address(c.getAddress())
      .parsedCvId(c.getParsedCvId())
      .skills(sk)
      .experiences(ex)
      .educations(ed)
      .languages(lg)
      .applicationsCount(c.getApplications() != null ? c.getApplications().size() : 0)
      .createdAt(c.getCreatedAt())
      .build();
  }
}
