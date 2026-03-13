package com.backend_core.recruforce2.service;

import com.backend_core.recruforce2.domain.entities.Application;
import com.backend_core.recruforce2.domain.entities.Candidate;
import com.backend_core.recruforce2.domain.entities.JobOffer;
import com.backend_core.recruforce2.domain.enums.ApplicationStatus;
import com.backend_core.recruforce2.dto.request.ApplicationRequest;
import com.backend_core.recruforce2.dto.response.ApplicationResponse;
import com.backend_core.recruforce2.dto.response.MatchingScoreResponse;
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
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for managing applications with AI-powered matching.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationService {

  private final ApplicationRepository applicationRepository;
  private final CandidateProfileRepository candidateRepository;
  private final JobOfferRepository jobOfferRepository;
  private final EmailService emailService;
  private final NotificationService notificationService;
  private final WebClient.Builder webClientBuilder;

  @Value("${ai.service.url}")
  private String aiServiceUrl;

  @Value("${matching.threshold:60}")
  private Integer matchingThreshold;

  /**
   * Submits a new application and triggers AI matching score calculation.
   */
  @Transactional
  public ApplicationResponse submit(ApplicationRequest request) {
    log.info("Submitting application: candidate={}, jobOffer={}",
      request.getCandidateId(), request.getJobOfferId());

    // Validate candidate and job offer exist
    Candidate candidate = candidateRepository.findById(request.getCandidateId())
      .orElseThrow(() -> new IllegalArgumentException("Candidate not found"));

    JobOffer jobOffer = jobOfferRepository.findById(request.getJobOfferId())
      .orElseThrow(() -> new IllegalArgumentException("Job offer not found"));

    // Check if already applied
    if (applicationRepository.existsByCandidateIdAndJobOfferId(
      request.getCandidateId(), request.getJobOfferId())) {
      throw new IllegalArgumentException("Application already exists");
    }

    // Create application
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

    // Send acknowledgment email
    emailService.sendApplicationAcknowledgment(
      candidate.getEmail(),
      candidate.getFullName(),
      jobOffer.getTitle()
    );

    // Calculate matching score asynchronously
    Long applicationId = application.getId();
    calculateMatchingScoreAsync(applicationId, candidate.getId(), jobOffer.getId());

    // Notify recruiter
    notificationService.notifyNewApplication(jobOffer.getCreatedBy(), application);

    return mapToResponse(application);
  }

  /**
   * Calculates the AI matching score for an application.
   */
  private void calculateMatchingScoreAsync(Long applicationId, Long candidateId, Long jobOfferId) {
    try {
      // Call AI microservice to calculate matching score
      MatchingScoreResponse matchingScore = callAiMatchingService(candidateId, jobOfferId);

      // Update application with score
      Application application = applicationRepository.findById(applicationId)
        .orElseThrow(() -> new IllegalArgumentException("Application not found"));

      application.analyzeScore(matchingScore.getMatchingScore(), matchingThreshold);
      applicationRepository.save(application);

      log.info("Matching score calculated: applicationId={}, score={}",
        applicationId, matchingScore.getMatchingScore());

    } catch (Exception e) {
      log.error("Failed to calculate matching score for application {}: {}",
        applicationId, e.getMessage());
    }
  }

  /**
   * Calls the AI microservice to get the matching score.
   */
  private MatchingScoreResponse callAiMatchingService(Long candidateId, Long jobOfferId) {
    Map<String, Object> request = new HashMap<>();
    request.put("candidate_id", candidateId);
    request.put("job_offer_id", jobOfferId);

    return webClientBuilder.build()
      .post()
      .uri(aiServiceUrl + "/api/match-score")
      .bodyValue(request)
      .retrieve()
      .bodyToMono(MatchingScoreResponse.class)
      .block();
  }

  /**
   * Changes the status of an application.
   */
  @Transactional
  public ApplicationResponse changeStatus(Long id, ApplicationStatus newStatus) {
    Application application = applicationRepository.findById(id)
      .orElseThrow(() -> new IllegalArgumentException("Application not found"));

    application.changeStatus(newStatus);
    return mapToResponse(applicationRepository.save(application));
  }

  @Transactional(readOnly = true)
  public ApplicationResponse getById(Long id) {
    Application application = applicationRepository.findById(id)
      .orElseThrow(() -> new IllegalArgumentException("Application not found"));
    return mapToResponse(application);
  }

  @Transactional(readOnly = true)
  public Page<ApplicationResponse> getByJobOffer(Long jobOfferId, Pageable pageable) {
    return applicationRepository.findByJobOfferId(jobOfferId, pageable)
      .map(this::mapToResponse);
  }

  @Transactional(readOnly = true)
  public Page<ApplicationResponse> getQualifiedByJobOffer(Long jobOfferId, Pageable pageable) {
    return applicationRepository.findQualifiedByJobOffer(jobOfferId)
      .stream()
      .map(this::mapToResponse)
      .collect(java.util.stream.Collectors.collectingAndThen(
        java.util.stream.Collectors.toList(),
        list -> new org.springframework.data.domain.PageImpl<>(
          list, pageable, list.size())
      ));
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
      .cvFilePath(application.getCvFilePath())
      .coverLetter(application.getCoverLetter())
      .sourceChannel(application.getSourceChannel())
      .build();
  }
}
