package com.backend_core.recruforce2.repository;

import com.backend_core.recruforce2.domain.entities.NotificationPreferences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * JPA repository for {@link NotificationPreferences} entity.
 * Provides access to the {@code notification_preferences} table in PostgreSQL.
 */
@Repository
public interface NotificationPreferencesRepository extends JpaRepository<NotificationPreferences, Long> {

  /** Finds notification preferences for a specific user */
  Optional<NotificationPreferences> findByUserId(Long userId);

  /** Checks if preferences already exist for a user */
  boolean existsByUserId(Long userId);
}
