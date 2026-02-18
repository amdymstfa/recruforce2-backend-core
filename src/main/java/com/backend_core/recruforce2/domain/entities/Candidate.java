package com.backend_core.recruforce2.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a job candidate with their full profile.
 */
@Entity
@Table(name = "candidates", indexes = {
  @Index(name = "idx_candidates_email", columnList = "email", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Candidate {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "First name is required")
  @Size(max = 100)
  @Column(name = "first_name", nullable = false, length = 100)
  private String firstName;

  @NotBlank(message = "Last name is required")
  @Size(max = 100)
  @Column(name = "last_name", nullable = false, length = 100)
  private String lastName;

  @Email(message = "Email must be valid")
  @NotBlank(message = "Email is required")
  @Column(nullable = false, unique = true, length = 150)
  private String email;

  @Size(max = 20)
  @Column(length = 20)
  private String phone;

  @Size(max = 250)
  @Column(length = 250)
  private String address;

  @Column(name = "birth_date")
  private LocalDate birthDate;

  /** File path of the uploaded CV (PDF or DOCX) */
  @Column(name = "cv_path", length = 500)
  private String cvPath;

  /** MongoDB document ID of the parsed CV (ParsedCvDocument._id) */
  @Column(name = "parsed_cv_id", length = 100)
  private String parsedCvId;

  /** Professional experiences */
  @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL,
    orphanRemoval = true, fetch = FetchType.LAZY)
  @Builder.Default
  private List<Experience> experiences = new ArrayList<>();

  /** Education entries */
  @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL,
    orphanRemoval = true, fetch = FetchType.LAZY)
  @Builder.Default
  private List<Education> educations = new ArrayList<>();

  /** Skills with proficiency levels */
  @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL,
    orphanRemoval = true, fetch = FetchType.LAZY)
  @Builder.Default
  private List<CandidateSkill> skills = new ArrayList<>();

  /** Languages spoken */
  @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL,
    orphanRemoval = true, fetch = FetchType.LAZY)
  @Builder.Default
  private List<Language> languages = new ArrayList<>();

  /** Applications submitted by this candidate */
  @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL,
    orphanRemoval = true, fetch = FetchType.LAZY)
  @Builder.Default
  private List<Application> applications = new ArrayList<>();

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  // -------------------------------------------------------
  // Lifecycle hooks
  // -------------------------------------------------------

  @PrePersist
  protected void onCreate() {
    this.createdAt = LocalDateTime.now();
  }

  // -------------------------------------------------------
  // Business methods
  // -------------------------------------------------------

  /** Returns the candidate's full name */
  public String getFullName() {
    return firstName + " " + lastName;
  }

  /** Creates or updates the candidate profile */
  public void updateProfile(String firstName, String lastName,
                            String phone, String address) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.phone = phone;
    this.address = address;
  }

  /** Links the uploaded CV file and its parsed MongoDB document */
  public void attachCv(String cvPath, String parsedCvId) {
    this.cvPath = cvPath;
    this.parsedCvId = parsedCvId;
  }

  /** Removes all personal data (GDPR right to erasure) */
  public void deleteGDPR() {
    this.firstName = "DELETED";
    this.lastName = "DELETED";
    this.email = "deleted_" + this.id + "@gdpr.removed";
    this.phone = null;
    this.address = null;
    this.birthDate = null;
    this.cvPath = null;
    this.parsedCvId = null;
  }
}
