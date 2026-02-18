package com.backend_core.recruforce2.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Records GDPR consent given by a candidate.
 * Tracks consent for data processing and storage as required by GDPR regulations.
 */
@Entity
@Table(name = "gdpr_consents", indexes = {
  @Index(name = "idx_gdpr_consent_candidate_id", columnList = "candidate_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GDPRConsent {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /** The candidate who gave or revoked consent */
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "candidate_id", nullable = false, unique = true)
  private Candidate candidate;

  /** Whether the candidate has consented to data processing */
  @Column(name = "consent_processing", nullable = false)
  @Builder.Default
  private Boolean consentProcessing = false;

  /** Whether the candidate has consented to data storage */
  @Column(name = "consent_storage", nullable = false)
  @Builder.Default
  private Boolean consentStorage = false;

  /** Date and time when consent was given or last updated */
  @Column(name = "consent_date")
  private LocalDateTime consentDate;

  /** IP address from which the consent was submitted */
  @Column(name = "ip_address", length = 45)
  private String ipAddress;

  // -------------------------------------------------------
  // Business methods
  // -------------------------------------------------------

  /** Records the candidate's consent */
  public void giveConsent(String ipAddress) {
    this.consentProcessing = true;
    this.consentStorage = true;
    this.consentDate = LocalDateTime.now();
    this.ipAddress = ipAddress;
  }

  /** Revokes the candidate's consent (triggers GDPR erasure process) */
  public void revokeConsent() {
    this.consentProcessing = false;
    this.consentStorage = false;
    this.consentDate = LocalDateTime.now();
  }
}
