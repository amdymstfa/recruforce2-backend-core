package com.backend_core.recruforce2.service;

import com.backend_core.recruforce2.domain.entities.MLModel;
import com.backend_core.recruforce2.dto.response.MLModelResponse;
import com.backend_core.recruforce2.repository.MLModelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MLModelService {

  private final MLModelRepository mlModelRepository;

  @Transactional(readOnly = true)
  public MLModelResponse getActiveModel() {
    return mlModelRepository.findByIsActiveTrue()
      .map(this::mapToResponse)
      .orElseThrow(() -> new IllegalArgumentException("No active ML model found"));
  }

  @Transactional(readOnly = true)
  public List<MLModelResponse> getAllModels() {
    return mlModelRepository.findAll().stream()
      .map(this::mapToResponse)
      .toList();
  }

  private MLModelResponse mapToResponse(MLModel model) {
    return MLModelResponse.builder()
      .id(model.getId())
      .name(model.getName())
      .version(model.getVersion())
      .algorithm(model.getAlgorithm())
      .trainingDate(model.getTrainingDate())
      .accuracy(model.getAccuracy())
      .precision(model.getPrecision())
      .recall(model.getRecall())
      .f1Score(model.getF1Score())
      .predictionsCount(model.getPredictionsCount())
      .successRate(model.getSuccessRate())
      .isActive(model.getIsActive())
      .statistics(model.getStatistics())
      .build();
  }
}
