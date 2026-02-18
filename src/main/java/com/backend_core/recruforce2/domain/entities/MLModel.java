package com.backend_core.recruforce2.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a trained machine learning model used for candidate matching and prediction.
 * Tracks versioning, performance metrics, and active status.
 */
@Entity
@Table(name = "ml_models", indexes = {
  @Index(name = "idx_ml_models_is_active", columnList = "is_active"),
  @Index(name = "idx_ml_models_name", columnList = "name")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MLModel {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "Model name is required")
  @Column(nullable = false, length = 100)
  private String name;

  /** Semantic version of the model (e.g. "1.0.0", "2.3.1") */
  @Column(nullable = false, length = 20)
  private String version;

  /** Algorithm used (e.g. RandomForest, LogisticRegression, XGBoost) */
  @Column(length = 100)
  private String algorithm;

  /** Date and time the model was trained */
  @Column(name = "training_date")
  private LocalDateTime trainingDate;

  /** Model accuracy on the test set (0.0 to 1.0) */
  @Column
  private Double accuracy;

  /** Model precision score */
  @Column
  private Double precision;

  /** Model recall score */
  @Column
  private Double recall;

  /** Model F1 score */
  @Column
  private Double f1Score;

  /** Total number of predictions made by this model */
  @Column(name = "predictions_count")
  @Builder.Default
  private Integer predictionsCount = 0;

  /** Success rate based on confirmed hires (0.0 to 1.0) */
  @Column(name = "success_rate")
  private Double successRate;

  /** Whether this is the currently active model used for predictions */
  @Column(name = "is_active", nullable = false)
  @Builder.Default
  private Boolean isActive = false;

  /** JSON-serialized model statistics and metadata */
  @Column(columnDefinition = "TEXT")
  private String statistics;

  /** Performance metrics tracked over time */
  @OneToMany(mappedBy = "model", cascade = CascadeType.ALL,
    orphanRemoval = true, fetch = FetchType.LAZY)
  @Builder.Default
  private List<ModelMetric> metrics = new ArrayList<>();

  // -------------------------------------------------------
  // Business methods
  // -------------------------------------------------------

  /** Trains the model — actual training is delegated to the Python microservice */
  public void train() {
    this.trainingDate = LocalDateTime.now();
  }

  /** Evaluates the model performance */
  public void evaluate(Double accuracy, Double precision, Double recall, Double f1Score) {
    this.accuracy = accuracy;
    this.precision = precision;
    this.recall = recall;
    this.f1Score = f1Score;
  }

  /** Activates this model as the current prediction model */
  public void activate() {
    this.isActive = true;
  }

  /** Deactivates this model */
  public void deactivate() {
    this.isActive = false;
  }

  /** Records a new prediction usage */
  public void predict() {
    this.predictionsCount++;
  }

  /** Saves updated model statistics */
  public void save(String statistics) {
    this.statistics = statistics;
  }
}
