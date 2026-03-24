package com.backend_core.recruforce2.controller;

import com.backend_core.recruforce2.dto.response.JobOfferResponse;
import com.backend_core.recruforce2.service.JobOfferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
@Tag(name = "Public", description = "Public endpoints - no auth required")
public class PublicController {

  private final JobOfferService jobOfferService;

  @Operation(summary = "Get all active job offers (public)")
  @GetMapping("/job-offers")
  public ResponseEntity<Page<JobOfferResponse>> getActiveOffers(
    @RequestParam(defaultValue = "0")  int page,
    @RequestParam(defaultValue = "12") int size
  ) {
    PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
    return ResponseEntity.ok(jobOfferService.getActiveOffers(pageable));
  }

  @Operation(summary = "Get a single job offer (public)")
  @GetMapping("/job-offers/{id}")
  public ResponseEntity<JobOfferResponse> getOffer(@PathVariable Long id) {
    return ResponseEntity.ok(jobOfferService.getById(id));
  }
}
