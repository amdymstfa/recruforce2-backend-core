package com.backend_core.recruforce2.service;

import com.backend_core.recruforce2.domain.entities.Criterion;
import com.backend_core.recruforce2.dto.request.CriteriaRequest;
import com.backend_core.recruforce2.dto.response.CriteriaResponse;
import com.backend_core.recruforce2.mapper.CriteriaMapper;
import com.backend_core.recruforce2.repository.CriterionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CriteriaService {

  private final CriterionRepository repository;

  public List<CriteriaResponse> getAll() {
    return repository.findAll()
      .stream()
      .map(CriteriaMapper::toResponse)
      .toList();
  }

  public CriteriaResponse create(CriteriaRequest request) {
    Criterion entity = CriteriaMapper.toEntity(request);
    return CriteriaMapper.toResponse(repository.save(entity));
  }

  public CriteriaResponse update(Long id, CriteriaRequest request) {
    Criterion entity = repository.findById(id)
      .orElseThrow(() -> new EntityNotFoundException("Criterion not found"));

    CriteriaMapper.updateEntity(entity, request);

    return CriteriaMapper.toResponse(repository.save(entity));
  }

  public void delete(Long id) {
    if (!repository.existsById(id)) {
      throw new EntityNotFoundException("Criterion not found");
    }
    repository.deleteById(id);
  }
}
