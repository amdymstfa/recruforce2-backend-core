package com.backend_core.recruforce2.service;

import com.backend_core.recruforce2.domain.entities.Candidate;
import com.backend_core.recruforce2.domain.entities.JobOffer;
import com.backend_core.recruforce2.domain.entities.Prediction;
import com.backend_core.recruforce2.dto.response.PredictionResponse;
import com.backend_core.recruforce2.repository.CandidateProfileRepository;
import com.backend_core.recruforce2.repository.JobOfferRepository;
import com.backend_core.recruforce2.repository.PredictionResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

/**
 * Service for AI-powered predictions.
 * Integrates with the Python AI microservice.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PredictionService {

  private final PredictionResultRepository predictionRepository;
  private final CandidateProfileRepository candidateRepository;
  private final JobOfferRepository jobOfferRepository;
  private final WebClient.Builder webClientBuilder;

  @Value("${ai.service.url}")
  private String aiServiceUrl;

  /**
   * Generates a prediction for a candidate/job offer pair.
   * Calls the AI microservice and stores the result.
   */
  @Transactional
  public PredictionResponse predict(Long candidateId, Long jobOfferId) {
    log.info("Generating prediction: candidateId={}, jobOfferId={}", candidateId, jobOfferId);

    Candidate candidate = candidateRepository.findById(candidateId)
      .orElseThrow(() -> new IllegalArgumentException("Candidate not found"));

    JobOffer jobOffer = jobOfferRepository.findById(jobOfferId)
      .orElseThrow(() -> new IllegalArgumentException("Job offer not found"));

    // Call AI microservice
    Map<String, Object> aiResponse = callAiPredictionService(candidateId, jobOfferId);

    // Create prediction entity
    Prediction prediction = Prediction.builder()
      .candidate(candidate)
      .jobOffer(jobOffer)
      .build();

    prediction.calculate(
      ((Number) aiResponse.get("matching_score")).doubleValue(),
      ((Number) aiResponse.get("success_probability")).doubleValue(),
      ((Number) aiResponse.get("confidence")).doubleValue(),
      (String) aiResponse.get("main_factors"),
      (String) aiResponse.get("recommendation")
    );

    prediction.setModelVersion((String) aiResponse.get("model_version"));

    prediction = predictionRepository.save(prediction);

    log.info("Prediction saved: id={}, score={}", prediction.getId(), prediction.getMatchingScore());

    return mapToResponse(prediction);
  }

  /**
   * Calls the AI microservice to generate a prediction.
   */
  private Map<String, Object> callAiPredictionService(Long candidateId, Long jobOfferId) {
    Map<String, Object> request = new HashMap<>();
    request.put("candidate_id", candidateId);
    request.put("job_offer_id", jobOfferId);

    return webClientBuilder.build()
      .post()
      .uri(aiServiceUrl + "/api/predict")
      .bodyValue(request)
      .retrieve()
      .bodyToMono(Map.class)
      .block();
  }

  @Transactional(readOnly = true)
  public PredictionResponse getById(Long id) {
    Prediction prediction = predictionRepository.findById(id)
      .orElseThrow(() -> new IllegalArgumentException("Prediction not found"));
    return mapToResponse(prediction);
  }

  @Transactional(readOnly = true)
  public PredictionResponse getLatestForCandidateAndJobOffer(Long candidateId, Long jobOfferId) {
    return predictionRepository.findTopByCandidateIdAndJobOfferIdOrderByCalculatedAtDesc(
        candidateId, jobOfferId)
      .map(this::mapToResponse)
      .orElse(null);
  }

  private PredictionResponse mapToResponse(Prediction prediction) {
    return PredictionResponse.builder()
      .id(prediction.getId())
      .candidateId(prediction.getCandidate().getId())
      .candidateName(prediction.getCandidate().getFullName())
      .jobOfferId(prediction.getJobOffer().getId())
      .jobOfferTitle(prediction.getJobOffer().getTitle())
      .matchingScore(prediction.getMatchingScore())
      .successProbability(prediction.getSuccessProbability())
      .confidence(prediction.getConfidence())
      .mainFactors(prediction.getMainFactors())
      .recommendation(prediction.getRecommendation())
      .calculatedAt(prediction.getCalculatedAt())
      .modelVersion(prediction.getModelVersion())
      .modelId(prediction.getModel() != null ? prediction.getModel().getId() : null)
      .build();
  }
}
