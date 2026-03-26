package com.backend_core.recruforce2.repository;

import com.backend_core.recruforce2.domain.entities.User;
import com.backend_core.recruforce2.domain.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA repository for {@link User} entity.
 * Provides access to the {@code users} table in PostgreSQL.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  /** Finds a user by their email address (used for authentication) */
  Optional<User> findByEmail(String email);

  /** Checks if a user with the given email already exists */
  boolean existsByEmail(String email);

  /** Finds all users with a specific role */
  List<User> findByRole(Role role);

  /** Finds all active users */
  List<User> findByIsActiveTrue();

  /** Finds all active users with a specific role */
  List<User> findByRoleAndIsActiveTrue(Role role);

  /** Finds a user by email and active status */
  Optional<User> findByEmailAndIsActiveTrue(String email);

  /** Searches users by first name or last name (case-insensitive) */
  @Query("SELECT u FROM User u WHERE " +
    "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
    "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
  List<User> searchByName(@Param("keyword") String keyword);

  /** Counts users by role */
  long countByRole(Role role);

  /** Counts active users */
  long countByIsActiveTrue();

  long countByRoleAndIsActiveTrue(Role role);

}
