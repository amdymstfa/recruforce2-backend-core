package com.backend_core.recruforce2.domain.entities;

import com.backend_core.recruforce2.domain.enums.ContractType;
import com.backend_core.recruforce2.domain.enums.OfferStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a job offer published by a recruiter.
 */
@Entity
@Table(name = "job_offers", indexes = {
  @Index(name = "idx_job_offers_status", columnList = "status"),
  @Index(name = "idx_job_offers_created_by", columnList = "created_by"),
  @Index(name = "idx_job_offers_expiration", columnList = "expiration_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobOffer {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "Job title is required")
  @Size(max = 200)
  @Column(nullable = false, length = 200)
  private String title;

  @NotBlank(message = "Job description is required")
  @Column(nullable = false, columnDefinition = "TEXT")
  private String description;

  @Size(max = 200)
  @Column(length = 200)
  private String location;

  @Enumerated(EnumType.STRING)
  @Column(name = "contract_type", nullable = false, length = 20)
  private ContractType contractType;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  @Builder.Default
  private OfferStatus status = OfferStatus.DRAFT;

  /** Minimum years of experience required */
  @Positive
  @Column(name = "min_experience")
  private Integer minExperience;

  /** Maximum years of experience accepted */
  @Positive
  @Column(name = "max_experience")
  private Integer maxExperience;

  /** Minimum salary offered */
  @Column(name = "min_salary")
  private Double minSalary;

  /** Maximum salary offered */
  @Column(name = "max_salary")
  private Double maxSalary;

  @Column(name = "publication_date")
  private LocalDate publicationDate;

  @Column(name = "expiration_date")
  private LocalDate expirationDate;

  /** Recruiter who created this offer */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "created_by", nullable = false)
  private User createdBy;

  /** Skills required for this position */
  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
    name = "job_offer_skills",
    joinColumns = @JoinColumn(name = "job_offer_id"),
    inverseJoinColumns = @JoinColumn(name = "skill_id")
  )
  @Builder.Default
  private List<Skill> requiredSkills = new ArrayList<>();

  /** Applications submitted for this offer */
  @OneToMany(mappedBy = "jobOffer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @Builder.Default
  private List<Application> applications = new ArrayList<>();

  /** Whether this offer was published on LinkedIn */
  @Column(name = "published_on_linkedin", nullable = false)
  @Builder.Default
  private Boolean publishedOnLinkedin = false;

  /** LinkedIn job post ID (if published externally) */
  @Column(name = "linkedin_job_id", length = 100)
  private String linkedinJobId;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  // -------------------------------------------------------
  // Lifecycle hooks
  // -------------------------------------------------------

  @PrePersist
  protected void onCreate() {
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
    this.updatedAt = LocalDateTime.now();
  }

  // -------------------------------------------------------
  // Business methods
  // -------------------------------------------------------

  /** Creates or updates the offer details */
  public void update(String title, String description, String location,
                     ContractType contractType, Integer minExperience,
                     Integer maxExperience, Double minSalary, Double maxSalary,
                     LocalDate expirationDate) {
    this.title = title;
    this.description = description;
    this.location = location;
    this.contractType = contractType;
    this.minExperience = minExperience;
    this.maxExperience = maxExperience;
    this.minSalary = minSalary;
    this.maxSalary = maxSalary;
    this.expirationDate = expirationDate;
  }

  /** Publishes the offer — sets status to ACTIVE and records the publication date */
  public void publish() {
    this.status = OfferStatus.ACTIVE;
    this.publicationDate = LocalDate.now();
  }

  /** Archives the offer */
  public void archive() {
    this.status = OfferStatus.ARCHIVED;
  }

  /** Marks the offer as published on LinkedIn */
  public void publishLinkedin(String linkedinJobId) {
    this.publishedOnLinkedin = true;
    this.linkedinJobId = linkedinJobId;
  }

  /** Returns the number of applications received for this offer */
  public int getStatistics() {
    return applications != null ? applications.size() : 0;
  }
}
