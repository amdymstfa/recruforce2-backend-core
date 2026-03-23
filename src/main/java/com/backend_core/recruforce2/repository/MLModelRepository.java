package com.backend_core.recruforce2.repository;

import com.backend_core.recruforce2.domain.entities.MLModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MLModelRepository extends JpaRepository<MLModel, Long> {
  Optional<MLModel> findByIsActiveTrue();
}
