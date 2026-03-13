package com.backend_core.recruforce2.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Tracks performance metrics of an ML model over time.
 * Enables monitoring of model drift and performance evolution.
 */
@Entity
@Table(name = "model_metrics", indexes = {
  @Index(name = "idx_model_metrics_model_id", columnList = "model_id"),
  @Index(name = "idx_model_metrics_recorded_at", columnList = "recorded_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModelMetric {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /** The ML model this metric belongs to */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "model_id", nullable = false)
  private MLModel model;

  /** Metric name (e.g. accuracy, precision, recall, f1_score, auc_roc) */
  @NotBlank(message = "Metric name is required")
  @Column(nullable = false, length = 100)
  private String metricName;

  /** Metric value at the time of recording */
  @Column(name = "metric_value", nullable = false)
  private Double metricValue;

  /** Number of predictions made when this metric was recorded */
  @Column(name = "predictions_count")
  private Integer predictionsCount;

  /** Date and time this metric was recorded */
  @Column(name = "recorded_at", nullable = false, updatable = false)
  private LocalDateTime recordedAt;

  /** Optional JSON statistics snapshot */
  @Column(columnDefinition = "TEXT")
  private String statistics;

  /** Whether this model version is still active */
  @Column(name = "is_active", nullable = false)
  @Builder.Default
  private Boolean isActive = true;

  // -------------------------------------------------------
  // Lifecycle hooks
  // -------------------------------------------------------

  @PrePersist
  protected void onCreate() {
    this.recordedAt = LocalDateTime.now();
  }
}
