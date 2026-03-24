package com.backend_core.recruforce2.service;

import com.backend_core.recruforce2.domain.entities.Application;
import com.backend_core.recruforce2.domain.entities.Candidate;
import com.backend_core.recruforce2.domain.entities.JobOffer;
import com.backend_core.recruforce2.domain.enums.ApplicationStatus;
import com.backend_core.recruforce2.dto.request.ApplicationRequest;
import com.backend_core.recruforce2.dto.response.ApplicationResponse;
import com.backend_core.recruforce2.repository.ApplicationRepository;
import com.backend_core.recruforce2.repository.CandidateProfileRepository;
import com.backend_core.recruforce2.repository.JobOfferRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationService {

  private final ApplicationRepository applicationRepository;
  private final CandidateProfileRepository candidateRepository;
  private final JobOfferRepository jobOfferRepository;
  private final EmailService emailService;
  private final NotificationService notificationService;
  private final RestTemplate restTemplate = new RestTemplate();

  @Value("${n8n.app-webhook:http://recruforce2-n8n:5678/webhook/application}")
  private String n8nAppWebhookUrl;

  @Value("${matching.threshold:60}")
  private Integer matchingThreshold;

  @Transactional
  public ApplicationResponse submit(ApplicationRequest request) {
    Candidate candidate = candidateRepository.findById(request.getCandidateId())
      .orElseThrow(() -> new IllegalArgumentException("Candidate not found"));

    JobOffer jobOffer = jobOfferRepository.findById(request.getJobOfferId())
      .orElseThrow(() -> new IllegalArgumentException("Job offer not found"));

    if (applicationRepository.existsByCandidateIdAndJobOfferId(request.getCandidateId(), request.getJobOfferId())) {
      throw new IllegalArgumentException("Application already exists");
    }

    Application application = Application.builder()
      .candidate(candidate)
      .jobOffer(jobOffer)
      .receivedAt(LocalDateTime.now())
      .status(ApplicationStatus.RECEIVED)
      .coverLetter(request.getCoverLetter())
      .sourceChannel(request.getSourceChannel())
      .isQualified(false)
      .build();

    application = applicationRepository.save(application);

    // Envoi du signal à n8n pour le traitement automatique (Score + Emails)
    triggerN8nApplicationWorkflow(application);

    return mapToResponse(application);
  }

  private void triggerN8nApplicationWorkflow(Application app) {
    try {
      Map<String, Object> payload = new HashMap<>();
      payload.put("applicationId", app.getId());
      payload.put("candidateId", app.getCandidate().getId());
      payload.put("jobOfferId", app.getJobOffer().getId());
      payload.put("candidateName", app.getCandidate().getFullName());
      payload.put("candidateEmail", app.getCandidate().getEmail());
      payload.put("jobTitle", app.getJobOffer().getTitle());

      restTemplate.postForEntity(n8nAppWebhookUrl, payload, String.class);
      log.info("N8N Application Workflow triggered for app: {}", app.getId());
    } catch (Exception e) {
      log.error("Failed to trigger N8N application workflow: {}", e.getMessage());
    }
  }

  @Transactional
  public void updateScore(Long id, Integer score) {
    Application app = applicationRepository.findById(id).orElseThrow();
    app.analyzeScore(score, matchingThreshold);
    applicationRepository.save(app);
  }

  @Transactional
  public ApplicationResponse changeStatus(Long id, ApplicationStatus newStatus) {
    Application app = applicationRepository.findById(id).orElseThrow();
    app.changeStatus(newStatus);
    return mapToResponse(applicationRepository.save(app));
  }

  @Transactional(readOnly = true)
  public ApplicationResponse getById(Long id) {
    return applicationRepository.findById(id).map(this::mapToResponse).orElseThrow();
  }

  @Transactional(readOnly = true)
  public Page<ApplicationResponse> getByJobOffer(Long jobOfferId, Pageable pageable) {
    return applicationRepository.findByJobOfferId(jobOfferId, pageable).map(this::mapToResponse);
  }

  private ApplicationResponse mapToResponse(Application application) {
    return ApplicationResponse.builder()
      .id(application.getId())
      .candidateId(application.getCandidate().getId())
      .candidateName(application.getCandidate().getFullName())
      .candidateEmail(application.getCandidate().getEmail())
      .jobOfferId(application.getJobOffer().getId())
      .jobOfferTitle(application.getJobOffer().getTitle())
      .receivedAt(application.getReceivedAt())
      .status(application.getStatus())
      .matchingScore(application.getMatchingScore())
      .isQualified(application.getIsQualified())
      .coverLetter(application.getCoverLetter())
      .sourceChannel(application.getSourceChannel())
      .build();
  }
}
