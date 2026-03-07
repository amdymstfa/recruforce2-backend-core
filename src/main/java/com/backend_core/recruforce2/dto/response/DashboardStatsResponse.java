package com.backend_core.recruforce2.dto.response;

import lombok.*;

import java.util.Map;

/**
 * Response DTO for dashboard statistics.
 */
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

  // Application status breakdown
  private Map<String, Long> applicationsByStatus;

  // Interview status breakdown
  private Map<String, Long> interviewsByStatus;

  // Average metrics
  private Double averageMatchingScore;
  private Double averageFeedbackScore;
}
