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

/**
 * Service for managing interviews.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class InterviewService {

  private final InterviewRepository interviewRepository;
  private final ApplicationRepository applicationRepository;
  private final UserRepository userRepository;
  private final EmailService emailService;
  private final NotificationService notificationService;

  /**
   * Schedules a new interview.
   */
  @Transactional
  public InterviewResponse schedule(InterviewRequest request) {
    log.info("Scheduling interview: applicationId={}, type={}",
      request.getApplicationId(), request.getType());

    Application application = applicationRepository.findById(request.getApplicationId())
      .orElseThrow(() -> new IllegalArgumentException("Application not found"));

    User interviewer = userRepository.findById(request.getInterviewerId())
      .orElseThrow(() -> new IllegalArgumentException("Interviewer not found"));

    // Check if interview of this type already exists
    interviewRepository.findByApplicationIdAndType(
        request.getApplicationId(), request.getType())
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

    // Send invitation email
    emailService.sendInterviewInvitation(interview);

    // Notify interviewer
    notificationService.notifyInterviewScheduled(interviewer, interview);

    return mapToResponse(interview);
  }

  /**
   * Confirms an interview (typically called by candidate via invitation link).
   */
  @Transactional
  public InterviewResponse confirm(String invitationToken) {
    Interview interview = interviewRepository.findByInvitationToken(invitationToken)
      .orElseThrow(() -> new IllegalArgumentException("Invalid invitation token"));

    interview.confirm();
    return mapToResponse(interviewRepository.save(interview));
  }

  /**
   * Cancels an interview.
   */
  @Transactional
  public void cancel(Long id) {
    Interview interview = interviewRepository.findById(id)
      .orElseThrow(() -> new IllegalArgumentException("Interview not found"));

    interview.cancel();
    interviewRepository.save(interview);
    log.info("Interview cancelled: {}", id);
  }

  /**
   * Reschedules an interview to a new date/time.
   */
  @Transactional
  public InterviewResponse reschedule(Long id, LocalDateTime newDateTime) {
    Interview interview = interviewRepository.findById(id)
      .orElseThrow(() -> new IllegalArgumentException("Interview not found"));

    interview.reschedule(newDateTime);
    interviewRepository.save(interview);

    // Send updated invitation
    emailService.sendInterviewInvitation(interview);

    return mapToResponse(interview);
  }

  /**
   * Sends reminders for upcoming interviews (scheduled job).
   */
  @Transactional
  public void sendUpcomingReminders() {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime in24h = now.plusHours(24);

    List<Interview> upcomingInterviews = interviewRepository.findNeedingReminders(now, in24h);

    upcomingInterviews.forEach(interview -> {
      emailService.sendInterviewReminder(interview);
      log.info("Reminder sent for interview: {}", interview.getId());
    });
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
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime inOneWeek = now.plusWeeks(1);

    return interviewRepository.findUpcomingByInterviewer(interviewerId, now, inOneWeek).stream()
      .map(this::mapToResponse)
      .collect(Collectors.toList());
  }

  private InterviewResponse mapToResponse(Interview interview) {
    return InterviewResponse.builder()
      .id(interview.getId())
      .applicationId(interview.getApplication().getId())
      .candidateEmail(interview.getApplication().getCandidate().getEmail())
      .candidateName(interview.getApplication().getCandidate().getFullName())
      .interviewerEmail(interview.getInterviewer().getEmail()) 
      .type(interview.getType())
      .status(interview.getStatus())
      .dateTime(interview.getDateTime())
      .durationMinutes(interview.getDurationMinutes())
      .location(interview.getLocation())
      .videoLink(interview.getVideoLink())
      .interviewerId(interview.getInterviewer().getId())
      .interviewerName(interview.getInterviewer().getFullName())
      .invitationToken(interview.getInvitationToken())
      .confirmationDate(interview.getConfirmationDate())
      .hasFeedback(interview.getFeedback() != null)
      .build();
  }
}
