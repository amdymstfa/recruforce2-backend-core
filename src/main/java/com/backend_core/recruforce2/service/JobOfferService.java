package com.backend_core.recruforce2.service;

import com.backend_core.recruforce2.domain.entities.JobOffer;
import com.backend_core.recruforce2.domain.entities.Skill;
import com.backend_core.recruforce2.domain.entities.User;
import com.backend_core.recruforce2.domain.enums.OfferStatus;
import com.backend_core.recruforce2.dto.request.JobOfferRequest;
import com.backend_core.recruforce2.dto.response.JobOfferResponse;
import com.backend_core.recruforce2.dto.response.SkillResponse;
import java.util.List;

import com.backend_core.recruforce2.repository.JobOfferRepository;
import com.backend_core.recruforce2.repository.SkillRepository;
import com.backend_core.recruforce2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for managing job offers.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class JobOfferService {

  private final JobOfferRepository jobOfferRepository;
  private final SkillRepository skillRepository;
  private final UserRepository userRepository;

  @Transactional
  public JobOfferResponse create(JobOfferRequest request, Long userId) {
    log.info("Creating new job offer: {}", request.getTitle());

    User createdBy = userRepository.findById(userId)
      .orElseThrow(() -> new IllegalArgumentException("User not found"));

    List<Skill> skills = skillRepository.findAllById(request.getRequiredSkillIds());

    JobOffer jobOffer = JobOffer.builder()
      .title(request.getTitle())
      .description(request.getDescription())
      .location(request.getLocation())
      .contractType(request.getContractType())
      .status(OfferStatus.DRAFT)
      .minExperience(request.getMinExperience())
      .maxExperience(request.getMaxExperience())
      .minSalary(request.getMinSalary())
      .maxSalary(request.getMaxSalary())
      .expirationDate(request.getExpirationDate())
      .createdBy(createdBy)
      .requiredSkills(skills)
      .publishedOnLinkedin(false)
      .createdAt(LocalDateTime.now())
      .updatedAt(LocalDateTime.now())
      .build();

    jobOffer = jobOfferRepository.save(jobOffer);

    if (Boolean.TRUE.equals(request.getPublishOnLinkedin())) {
      publishToLinkedIn(jobOffer);
    }

    return mapToResponse(jobOffer);
  }

  @Transactional
  public JobOfferResponse update(Long id, JobOfferRequest request) {
    JobOffer jobOffer = findById(id);

    jobOffer.update(
      request.getTitle(),
      request.getDescription(),
      request.getLocation(),
      request.getContractType(),
      request.getMinExperience(),
      request.getMaxExperience(),
      request.getMinSalary(),
      request.getMaxSalary(),
      request.getExpirationDate()
    );

    if (request.getRequiredSkillIds() != null) {
      List<Skill> skills = skillRepository.findAllById(request.getRequiredSkillIds());
      jobOffer.setRequiredSkills(skills);
    }

    return mapToResponse(jobOfferRepository.save(jobOffer));
  }

  @Transactional
  public void publish(Long id) {
    JobOffer jobOffer = findById(id);
    jobOffer.publish();
    jobOfferRepository.save(jobOffer);
    log.info("Job offer published: {}", id);
  }

  @Transactional
  public void archive(Long id) {
    JobOffer jobOffer = findById(id);
    jobOffer.archive();
    jobOfferRepository.save(jobOffer);
  }

  @Transactional(readOnly = true)
  public JobOfferResponse getById(Long id) {
    return mapToResponse(findById(id));
  }

  @Transactional(readOnly = true)
  public Page<JobOfferResponse> getAll(Pageable pageable) {
    return jobOfferRepository.findAll(pageable).map(this::mapToResponse);
  }

  @Transactional(readOnly = true)
  public Page<JobOfferResponse> searchByKeyword(String keyword, Pageable pageable) {
    return jobOfferRepository.searchByKeyword(keyword, pageable).map(this::mapToResponse);
  }

  private void publishToLinkedIn(JobOffer jobOffer) {
    // TODO: Integrate with LinkedIn API via N8N webhook
    log.info("Publishing job offer to LinkedIn: {}", jobOffer.getId());
  }

  private JobOffer findById(Long id) {
    return jobOfferRepository.findById(id)
      .orElseThrow(() -> new IllegalArgumentException("Job offer not found: " + id));
  }

  private JobOfferResponse mapToResponse(JobOffer jobOffer) {
    List<SkillResponse> requiredSkills = jobOffer.getRequiredSkills() != null
      ? jobOffer.getRequiredSkills().stream()
          .map(s -> SkillResponse.builder()
            .id(s.getId())
            .name(s.getName())
            .type(s.getType())
            .requiredLevel(s.getRequiredLevel())
            .build())
          .toList()
      : List.of();

    return JobOfferResponse.builder()
      .id(jobOffer.getId())
      .title(jobOffer.getTitle())
      .description(jobOffer.getDescription())
      .location(jobOffer.getLocation())
      .contractType(jobOffer.getContractType())
      .status(jobOffer.getStatus())
      .minExperience(jobOffer.getMinExperience())
      .maxExperience(jobOffer.getMaxExperience())
      .minSalary(jobOffer.getMinSalary())
      .maxSalary(jobOffer.getMaxSalary())
      .publicationDate(jobOffer.getPublicationDate())
      .expirationDate(jobOffer.getExpirationDate())
      .createdById(jobOffer.getCreatedBy().getId())
      .createdByName(jobOffer.getCreatedBy().getFullName())
      .requiredSkills(requiredSkills)
      .publishedOnLinkedin(jobOffer.getPublishedOnLinkedin())
      .applicationsCount(jobOffer.getStatistics())
      .createdAt(jobOffer.getCreatedAt())
      .updatedAt(jobOffer.getUpdatedAt())
      .build();
  }
}
