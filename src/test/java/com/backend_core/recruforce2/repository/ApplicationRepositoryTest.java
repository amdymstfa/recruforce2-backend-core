package com.backend_core.recruforce2.repository;

import com.backend_core.recruforce2.domain.entities.Application;
import com.backend_core.recruforce2.domain.entities.Candidate;
import com.backend_core.recruforce2.domain.entities.JobOffer;
import com.backend_core.recruforce2.domain.entities.User;
import com.backend_core.recruforce2.domain.enums.ApplicationStatus;
import com.backend_core.recruforce2.domain.enums.ContractType;
import com.backend_core.recruforce2.domain.enums.OfferStatus;
import com.backend_core.recruforce2.domain.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class ApplicationRepositoryTest {

  @Autowired
  private ApplicationRepository applicationRepository;

  @Autowired
  private CandidateProfileRepository candidateRepository;

  @Autowired
  private JobOfferRepository jobOfferRepository;

  @Autowired
  private UserRepository userRepository;

  private Candidate testCandidate;
  private JobOffer testJobOffer;
  private Application testApplication;

  @BeforeEach
  void setUp() {
    applicationRepository.deleteAll();
    candidateRepository.deleteAll();
    jobOfferRepository.deleteAll();
    userRepository.deleteAll();

    // Create test user
    User recruiter = User.builder()
      .email("recruiter@example.com")
      .password("password")
      .firstName("Recruiter")
      .lastName("User")
      .role(Role.RECRUITER)
      .isActive(true)
      .createdAt(LocalDateTime.now())
      .build();
    recruiter = userRepository.save(recruiter);

    // Create test candidate
    testCandidate = Candidate.builder()
      .firstName("Jane")
      .lastName("Smith")
      .email("jane@example.com")
      .createdAt(LocalDateTime.now())
      .build();
    testCandidate = candidateRepository.save(testCandidate);

    // Create test job offer
    testJobOffer = JobOffer.builder()
      .title("Software Engineer")
      .description("Great opportunity")
      .contractType(ContractType.CDI)
      .status(OfferStatus.ACTIVE)
      .createdBy(recruiter)
      .createdAt(LocalDateTime.now())
      .build();
    testJobOffer = jobOfferRepository.save(testJobOffer);

    // Create test application
    testApplication = Application.builder()
      .candidate(testCandidate)
      .jobOffer(testJobOffer)
      .receivedAt(LocalDateTime.now())
      .status(ApplicationStatus.RECEIVED)
      .matchingScore(75)
      .isQualified(true)
      .build();
  }

  @Test
  void save_Success() {
    // When
    Application saved = applicationRepository.save(testApplication);

    // Then
    assertThat(saved).isNotNull();
    assertThat(saved.getId()).isNotNull();
    assertThat(saved.getMatchingScore()).isEqualTo(75);
  }

  @Test
  void findByCandidateId_Success() {
    // Given
    applicationRepository.save(testApplication);

    // When
    List<Application> applications = applicationRepository.findByCandidateId(testCandidate.getId());

    // Then
    assertThat(applications).hasSize(1);
    assertThat(applications.get(0).getCandidate().getEmail()).isEqualTo("jane@example.com");
  }

  @Test
  void existsByCandidateIdAndJobOfferId_ReturnsTrue() {
    // Given
    applicationRepository.save(testApplication);

    // When
    boolean exists = applicationRepository.existsByCandidateIdAndJobOfferId(
      testCandidate.getId(), testJobOffer.getId());

    // Then
    assertThat(exists).isTrue();
  }

  @Test
  void existsByCandidateIdAndJobOfferId_ReturnsFalse() {
    // When
    boolean exists = applicationRepository.existsByCandidateIdAndJobOfferId(999L, 999L);

    // Then
    assertThat(exists).isFalse();
  }

  @Test
  void findByStatus_Success() {
    // Given
    applicationRepository.save(testApplication);

    Application acceptedApp = Application.builder()
      .candidate(testCandidate)
      .jobOffer(testJobOffer)
      .receivedAt(LocalDateTime.now())
      .status(ApplicationStatus.ACCEPTED)
      .isQualified(true)
      .build();
    applicationRepository.save(acceptedApp);

    // When
    List<Application> receivedApps = applicationRepository.findByJobOfferIdAndStatus(
      testJobOffer.getId(), ApplicationStatus.RECEIVED);

    // Then
    assertThat(receivedApps).hasSize(1);
    assertThat(receivedApps.get(0).getStatus()).isEqualTo(ApplicationStatus.RECEIVED);
  }

  @Test
  void findQualifiedByJobOffer_Success() {
    // Given
    testApplication.setMatchingScore(85);
    testApplication.setIsQualified(true);
    applicationRepository.save(testApplication);

    Application lowScoreApp = Application.builder()
      .candidate(testCandidate)
      .jobOffer(testJobOffer)
      .receivedAt(LocalDateTime.now())
      .status(ApplicationStatus.RECEIVED)
      .matchingScore(45)
      .isQualified(false)
      .build();
    applicationRepository.save(lowScoreApp);

    // When
    List<Application> qualified = applicationRepository.findQualifiedByJobOffer(testJobOffer.getId());

    // Then
    assertThat(qualified).hasSize(1);
    assertThat(qualified.get(0).getIsQualified()).isTrue();
    assertThat(qualified.get(0).getMatchingScore()).isGreaterThan(60);
  }
}
