package com.backend_core.recruforce2.dto.response;

import lombok.*;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardStatsResponse {

  // User statistics
  private Long totalUsers;
  private Long activeRecruiters;
  private Long activeManagers;

  // Job offer statistics
  private Long totalJobOffers;
  private Long activeJobOffers;
  private Long draftJobOffers;
  private Long archivedJobOffers;

  // Application statistics
  private Long totalApplications;
  private Long applicationsToday;
  private Long qualifiedApplications;
  private Long rejectedApplications;

  // Candidate statistics
  private Long totalCandidates;
  private Long candidatesWithParsedCv;

  // Interview statistics
  private Long totalInterviews;
  private Long upcomingInterviews;
  private Long completedInterviews;

  // Status breakdowns
  private Map<String, Long> applicationsByStatus;
  private Map<String, Long> interviewsByStatus;

  // Averages
  private Double averageMatchingScore;
  private Double averageFeedbackScore;

  private List<ApplicationResponse> recentApplications;
}
