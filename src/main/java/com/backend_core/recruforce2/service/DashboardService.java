package com.backend_core.recruforce2.service;

import com.backend_core.recruforce2.dto.response.ApplicationResponse;
import com.backend_core.recruforce2.dto.response.DashboardStatsResponse;
import com.backend_core.recruforce2.domain.enums.*;
import com.backend_core.recruforce2.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

  private final UserRepository            userRepository;
  private final JobOfferRepository        jobOfferRepository;
  private final ApplicationRepository     applicationRepository;
  private final CandidateProfileRepository candidateRepository;
  private final InterviewRepository       interviewRepository;
  private final InterviewFeedbackRepository feedbackRepository;
  private final ApplicationService        applicationService;

  @Transactional(readOnly = true)
  public DashboardStatsResponse getGlobalStats() {

    List<ApplicationResponse> recent = applicationRepository
      .findAll(PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "receivedAt")))
      .stream()
      .map(applicationService::mapToResponsePublic)
      .collect(Collectors.toList());

    return DashboardStatsResponse.builder()
      .totalUsers(userRepository.count())
      .activeRecruiters(userRepository.countByRoleAndIsActiveTrue(Role.RECRUITER))
      .activeManagers(userRepository.countByRoleAndIsActiveTrue(Role.MANAGER))

      .totalJobOffers(jobOfferRepository.count())
      .activeJobOffers(jobOfferRepository.countByStatus(OfferStatus.ACTIVE))
      .draftJobOffers(jobOfferRepository.countByStatus(OfferStatus.DRAFT))
      .archivedJobOffers(jobOfferRepository.countByStatus(OfferStatus.ARCHIVED))

      .totalApplications(applicationRepository.count())
      .applicationsToday(applicationRepository.countApplicationsToday())
      .qualifiedApplications(applicationRepository.countByIsQualifiedTrue())
      .rejectedApplications(applicationRepository.countByStatus(ApplicationStatus.REJECTED))

      .totalCandidates(candidateRepository.count())
      .candidatesWithParsedCv(candidateRepository.countByParsedCvIdIsNotNull())

      .totalInterviews(interviewRepository.count())
      .upcomingInterviews(interviewRepository.countUpcomingInterviews())
      .completedInterviews(interviewRepository.countByStatus(InterviewStatus.COMPLETED))

      .applicationsByStatus(formatStatusMap(applicationRepository.countApplicationsByStatus()))
      .interviewsByStatus(formatStatusMap(interviewRepository.countInterviewsByStatus()))

      .averageMatchingScore(applicationRepository.getAverageMatchingScore())
      .averageFeedbackScore(feedbackRepository.findAverageOverallScore())

      .recentApplications(recent)
      .build();
  }

  private Map<String, Long> formatStatusMap(java.util.List<Object[]> results) {
    return results.stream()
      .collect(Collectors.toMap(
        row -> row[0].toString(),
        row -> (Long) row[1]
      ));
  }
}
