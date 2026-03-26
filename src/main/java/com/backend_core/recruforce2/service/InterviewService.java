package com.backend_core.recruforce2.service;

import com.backend_core.recruforce2.domain.entities.Application;
import com.backend_core.recruforce2.domain.entities.Interview;
import com.backend_core.recruforce2.domain.entities.User;
import com.backend_core.recruforce2.domain.enums.InterviewStatus;
import com.backend_core.recruforce2.dto.request.InterviewRequest;
import com.backend_core.recruforce2.dto.response.InterviewResponse;
import com.backend_core.recruforce2.repository.ApplicationRepository;
import com.backend_core.recruforce2.repository.InterviewRepository;
import com.backend_core.recruforce2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InterviewService {

  private final InterviewRepository interviewRepository;
  private final ApplicationRepository applicationRepository;
  private final UserRepository userRepository;
  private final EmailService emailService;
  private final NotificationService notificationService;

  @Transactional
  public InterviewResponse schedule(InterviewRequest request) {
    log.info("Scheduling interview: applicationId={}, type={}", request.getApplicationId(), request.getType());

    Application application = applicationRepository.findById(request.getApplicationId())
      .orElseThrow(() -> new IllegalArgumentException("Application not found"));

    User interviewer = userRepository.findById(request.getInterviewerId())
      .orElseThrow(() -> new IllegalArgumentException("Interviewer not found"));

    interviewRepository.findByApplicationIdAndType(request.getApplicationId(), request.getType())
      .ifPresent(i -> {
        throw new IllegalArgumentException("Interview already scheduled");
      });

    Interview interview = Interview.builder()
      .application(application)
      .type(request.getType())
      .status(InterviewStatus.SCHEDULED)
      .dateTime(request.getDateTime())
      .durationMinutes(request.getDurationMinutes() != null ? request.getDurationMinutes() : 60)
      .location(request.getLocation())
      .videoLink(request.getVideoLink())
      .interviewer(interviewer)
      .build();

    interview = interviewRepository.save(interview);
    emailService.sendInterviewInvitation(interview);
    notificationService.notifyInterviewScheduled(interviewer, interview);

    return mapToResponse(interview);
  }

  @Transactional
  public InterviewResponse confirm(String invitationToken) {
    Interview interview = interviewRepository.findByInvitationToken(invitationToken)
      .orElseThrow(() -> new IllegalArgumentException("Invalid invitation token"));

    interview.confirm();
    return mapToResponse(interviewRepository.save(interview));
  }

  @Transactional
  public void cancel(Long id) {
    Interview interview = interviewRepository.findById(id)
      .orElseThrow(() -> new IllegalArgumentException("Interview not found"));

    interview.cancel();
    interviewRepository.save(interview);
  }

  @Transactional
  public InterviewResponse reschedule(Long id, LocalDateTime newDateTime) {
    Interview interview = interviewRepository.findById(id)
      .orElseThrow(() -> new IllegalArgumentException("Interview not found"));

    interview.reschedule(newDateTime);
    interviewRepository.save(interview);
    emailService.sendInterviewInvitation(interview);

    return mapToResponse(interview);
  }

  @Transactional
  public void sendUpcomingReminders() {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime in24h = now.plusHours(24);
    List<Interview> upcomingInterviews = interviewRepository.findNeedingReminders(now, in24h);
    upcomingInterviews.forEach(emailService::sendInterviewReminder);
  }

  @Transactional(readOnly = true)
  public List<InterviewResponse> getAll() {
    return interviewRepository.findAll().stream()
      .map(this::mapToResponse)
      .toList();
  }

  @Transactional(readOnly = true)
  public InterviewResponse getById(Long id) {
    Interview interview = interviewRepository.findById(id)
      .orElseThrow(() -> new IllegalArgumentException("Interview not found"));
    return mapToResponse(interview);
  }

  @Transactional(readOnly = true)
  public List<InterviewResponse> getByApplication(Long applicationId) {
    return interviewRepository.findByApplicationId(applicationId).stream()
      .map(this::mapToResponse)
      .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public List<InterviewResponse> getUpcomingByInterviewer(Long interviewerId) {
    return interviewRepository.findUpcomingByInterviewer(interviewerId, LocalDateTime.now().minusDays(1)).stream()
      .map(this::mapToResponse)
      .collect(Collectors.toList());
  }

  private InterviewResponse mapToResponse(Interview interview) {
    if (interview == null) return null;

    InterviewResponse.InterviewResponseBuilder builder = InterviewResponse.builder()
      .id(interview.getId())
      .type(interview.getType())
      .status(interview.getStatus())
      .dateTime(interview.getDateTime())
      .durationMinutes(interview.getDurationMinutes())
      .location(interview.getLocation())
      .videoLink(interview.getVideoLink())
      .invitationToken(interview.getInvitationToken())
      .confirmationDate(interview.getConfirmationDate())
      .hasFeedback(interview.getFeedback() != null);

    if (interview.getApplication() != null) {
      builder.applicationId(interview.getApplication().getId());
      if (interview.getApplication().getCandidate() != null) {
        builder.candidateEmail(interview.getApplication().getCandidate().getEmail());
        builder.candidateName(interview.getApplication().getCandidate().getFullName());
      }
    }

    if (interview.getInterviewer() != null) {
      builder.interviewerId(interview.getInterviewer().getId());
      builder.interviewerName(interview.getInterviewer().getFullName());
      builder.interviewerEmail(interview.getInterviewer().getEmail());
    }

    return builder.build();
  }
}
