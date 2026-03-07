package com.backend_core.recruforce2.service;

import com.backend_core.recruforce2.domain.entities.Application;
import com.backend_core.recruforce2.domain.entities.Candidate;
import com.backend_core.recruforce2.domain.entities.JobOffer;
import com.backend_core.recruforce2.domain.enums.ApplicationStatus;
import com.backend_core.recruforce2.dto.request.ApplicationRequest;
import com.backend_core.recruforce2.dto.response.ApplicationResponse;
import com.backend_core.recruforce2.repository.ApplicationRepository;
import com.backend_core.recruforce2.repository.CandidateProfileRepository;
import com.backend_core.recruforce2.repository.JobOfferRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {

  @Mock
  private ApplicationRepository applicationRepository;

  @Mock
  private CandidateProfileRepository candidateRepository;

  @Mock
  private JobOfferRepository jobOfferRepository;

  @Mock
  private EmailService emailService;

  @Mock
  private NotificationService notificationService;

  @InjectMocks
  private ApplicationService applicationService;

  private Candidate testCandidate;
  private JobOffer testJobOffer;
  private Application testApplication;
  private ApplicationRequest applicationRequest;

  @BeforeEach
  void setUp() {
    testCandidate = Candidate.builder()
      .id(1L)
      .firstName("Jane")
      .lastName("Smith")
      .email("jane@example.com")
      .createdAt(LocalDateTime.now())
      .build();

    testJobOffer = JobOffer.builder()
      .id(1L)
      .title("Software Engineer")
      .build();

    testApplication = Application.builder()
      .id(1L)
      .candidate(testCandidate)
      .jobOffer(testJobOffer)
      .receivedAt(LocalDateTime.now())
      .status(ApplicationStatus.RECEIVED)
      .isQualified(false)
      .build();

    applicationRequest = ApplicationRequest.builder()
      .candidateId(1L)
      .jobOfferId(1L)
      .coverLetter("I'm interested in this position")
      .build();
  }

  @Test
  void submit_Success() {
    // Given
    when(candidateRepository.findById(anyLong())).thenReturn(Optional.of(testCandidate));
    when(jobOfferRepository.findById(anyLong())).thenReturn(Optional.of(testJobOffer));
    when(applicationRepository.existsByCandidateIdAndJobOfferId(anyLong(), anyLong())).thenReturn(false);
    when(applicationRepository.save(any(Application.class))).thenReturn(testApplication);

    // When
    ApplicationResponse response = applicationService.submit(applicationRequest);

    // Then
    assertThat(response).isNotNull();
    assertThat(response.getCandidateName()).isEqualTo("Jane Smith");
    assertThat(response.getJobOfferTitle()).isEqualTo("Software Engineer");
    assertThat(response.getStatus()).isEqualTo(ApplicationStatus.RECEIVED);

    verify(applicationRepository).save(any(Application.class));
    verify(emailService).sendApplicationAcknowledgment(anyString(), anyString(), anyString());
  }

  @Test
  void submit_AlreadyApplied_ThrowsException() {
    // Given
    when(candidateRepository.findById(anyLong())).thenReturn(Optional.of(testCandidate));
    when(jobOfferRepository.findById(anyLong())).thenReturn(Optional.of(testJobOffer));
    when(applicationRepository.existsByCandidateIdAndJobOfferId(anyLong(), anyLong())).thenReturn(true);

    // When / Then
    assertThatThrownBy(() -> applicationService.submit(applicationRequest))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("Application already exists");

    verify(applicationRepository, never()).save(any());
  }

  @Test
  void changeStatus_Success() {
    // Given
    when(applicationRepository.findById(anyLong())).thenReturn(Optional.of(testApplication));
    when(applicationRepository.save(any(Application.class))).thenReturn(testApplication);

    // When
    ApplicationResponse response = applicationService.changeStatus(1L, ApplicationStatus.ACCEPTED);

    // Then
    assertThat(response).isNotNull();
    verify(applicationRepository).save(any(Application.class));
  }

  @Test
  void getById_Success() {
    // Given
    when(applicationRepository.findById(anyLong())).thenReturn(Optional.of(testApplication));

    // When
    ApplicationResponse response = applicationService.getById(1L);

    // Then
    assertThat(response).isNotNull();
    assertThat(response.getId()).isEqualTo(1L);
    assertThat(response.getCandidateEmail()).isEqualTo("jane@example.com");
  }

  @Test
  void getById_NotFound_ThrowsException() {
    // Given
    when(applicationRepository.findById(anyLong())).thenReturn(Optional.empty());

    // When / Then
    assertThatThrownBy(() -> applicationService.getById(1L))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("Application not found");
  }
}
