package com.backend_core.recruforce2.service;

import com.backend_core.recruforce2.domain.entities.*;
import com.backend_core.recruforce2.dto.request.FeedbackRequest;
import com.backend_core.recruforce2.dto.response.FeedbackResponse;
import com.backend_core.recruforce2.mapper.FeedbackMapper;
import com.backend_core.recruforce2.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedbackService {

  private final FeedbackRepository feedbackRepository;
  private final InterviewRepository interviewRepository;
  private final UserRepository userRepository;
  private final CriterionRepository criterionRepository;
  private final FeedbackMapper feedbackMapper;

  // -----------------------------
  // CREATE
  // -----------------------------
  public FeedbackResponse create(FeedbackRequest request) {

    feedbackRepository.findByInterviewId(request.getInterviewId())
      .ifPresent(f -> {
        throw new IllegalStateException("Feedback already exists for this interview");
      });

    Interview interview = interviewRepository.findById(request.getInterviewId())
      .orElseThrow(() -> new EntityNotFoundException("Interview not found"));

    User evaluator = userRepository.findById(request.getEvaluatorId())
      .orElseThrow(() -> new EntityNotFoundException("Evaluator not found"));

    Feedback feedback = Feedback.builder()
      .interview(interview)
      .evaluator(evaluator)
      .generalComment(request.getGeneralComment())
      .recommendation(request.getRecommendation())
      .build();

    addCriteria(feedback, request);

    feedback.calculateOverallScore();

    return feedbackMapper.toResponse(feedbackRepository.save(feedback));
  }

  // -----------------------------
  // GET ALL
  // -----------------------------
  public List<FeedbackResponse> getAll() {
    return feedbackRepository.findAll()
      .stream()
      .map(feedbackMapper::toResponse)
      .toList();
  }

  // -----------------------------
  // UPDATE
  // -----------------------------
  public FeedbackResponse update(Long id, FeedbackRequest request) {

    Feedback feedback = feedbackRepository.findById(id)
      .orElseThrow(() -> new EntityNotFoundException("Feedback not found"));

    feedback.update(request.getGeneralComment(), request.getRecommendation());

    // reset
    feedback.getCriteriaEvaluations().clear();

    addCriteria(feedback, request);

    feedback.calculateOverallScore();

    return feedbackMapper.toResponse(feedbackRepository.save(feedback));
  }

  // -----------------------------
  // DELETE
  // -----------------------------
  public void delete(Long id) {
    if (!feedbackRepository.existsById(id)) {
      throw new EntityNotFoundException("Feedback not found");
    }
    feedbackRepository.deleteById(id);
  }

  // -----------------------------
  // PRIVATE HELPER 🔥
  // -----------------------------
  private void addCriteria(Feedback feedback, FeedbackRequest request) {

    if (request.getCriteria() == null) return;

    for (var c : request.getCriteria()) {

      if (c.getScore() == null || c.getScore() < 0 || c.getScore() > 100) {
        throw new IllegalArgumentException("Score must be between 0 and 100");
      }

      Criterion criterion = criterionRepository.findById(c.getId())
        .orElseThrow(() -> new EntityNotFoundException("Criterion not found"));

      if (!criterion.getInterviewType().equals(feedback.getInterview().getType())) {
        throw new IllegalArgumentException("Criterion not valid for this interview type");
      }

      EvaluationCriterion ec = EvaluationCriterion.builder()
        .criterion(criterion)
        .score(c.getScore())
        .build();

      feedback.add(ec);
    }
  }

  public FeedbackResponse getByInterviewId(Long interviewId) {
    Feedback feedback = feedbackRepository.findByInterviewId(interviewId)
      .orElseThrow(() -> new EntityNotFoundException("Feedback not found"));

    return feedbackMapper.toResponse(feedback);
  }
}
