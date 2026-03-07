package com.backend_core.recruforce2.repository;

import com.backend_core.recruforce2.domain.entities.User;
import com.backend_core.recruforce2.domain.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  private User testUser;

  @BeforeEach
  void setUp() {
    userRepository.deleteAll();

    testUser = User.builder()
      .email("test@example.com")
      .password("encoded_password")
      .firstName("John")
      .lastName("Doe")
      .phone("1234567890")
      .role(Role.RECRUITER)
      .isActive(true)
      .createdAt(LocalDateTime.now())
      .build();
  }

  @Test
  void save_Success() {
    // When
    User savedUser = userRepository.save(testUser);

    // Then
    assertThat(savedUser).isNotNull();
    assertThat(savedUser.getId()).isNotNull();
    assertThat(savedUser.getEmail()).isEqualTo("test@example.com");
  }

  @Test
  void findByEmail_Success() {
    // Given
    userRepository.save(testUser);

    // When
    Optional<User> found = userRepository.findByEmail("test@example.com");

    // Then
    assertThat(found).isPresent();
    assertThat(found.get().getFirstName()).isEqualTo("John");
  }

  @Test
  void findByEmail_NotFound() {
    // When
    Optional<User> found = userRepository.findByEmail("nonexistent@example.com");

    // Then
    assertThat(found).isEmpty();
  }

  @Test
  void existsByEmail_ReturnsTrue() {
    // Given
    userRepository.save(testUser);

    // When
    boolean exists = userRepository.existsByEmail("test@example.com");

    // Then
    assertThat(exists).isTrue();
  }

  @Test
  void existsByEmail_ReturnsFalse() {
    // When
    boolean exists = userRepository.existsByEmail("nonexistent@example.com");

    // Then
    assertThat(exists).isFalse();
  }

  @Test
  void findByRole_Success() {
    // Given
    userRepository.save(testUser);

    User adminUser = User.builder()
      .email("admin@example.com")
      .password("password")
      .firstName("Admin")
      .lastName("User")
      .role(Role.ADMIN)
      .isActive(true)
      .createdAt(LocalDateTime.now())
      .build();
    userRepository.save(adminUser);

    // When
    List<User> recruiters = userRepository.findByRole(Role.RECRUITER);
    List<User> admins = userRepository.findByRole(Role.ADMIN);

    // Then
    assertThat(recruiters).hasSize(1);
    assertThat(recruiters.get(0).getEmail()).isEqualTo("test@example.com");
    assertThat(admins).hasSize(1);
    assertThat(admins.get(0).getEmail()).isEqualTo("admin@example.com");
  }

  @Test
  void findByIsActiveTrue_Success() {
    // Given
    userRepository.save(testUser);

    User inactiveUser = User.builder()
      .email("inactive@example.com")
      .password("password")
      .firstName("Inactive")
      .lastName("User")
      .role(Role.RECRUITER)
      .isActive(false)
      .createdAt(LocalDateTime.now())
      .build();
    userRepository.save(inactiveUser);

    // When
    List<User> activeUsers = userRepository.findByIsActiveTrue();

    // Then
    assertThat(activeUsers).hasSize(1);
    assertThat(activeUsers.get(0).getEmail()).isEqualTo("test@example.com");
  }

  @Test
  void searchByName_Success() {
    // Given
    userRepository.save(testUser);

    // When
    List<User> results = userRepository.searchByName("john");

    // Then
    assertThat(results).hasSize(1);
    assertThat(results.get(0).getFirstName()).isEqualTo("John");
  }

  @Test
  void countByRole_Success() {
    // Given
    userRepository.save(testUser);

    User anotherRecruiter = User.builder()
      .email("recruiter2@example.com")
      .password("password")
      .firstName("Jane")
      .lastName("Smith")
      .role(Role.RECRUITER)
      .isActive(true)
      .createdAt(LocalDateTime.now())
      .build();
    userRepository.save(anotherRecruiter);

    // When
    long count = userRepository.countByRole(Role.RECRUITER);

    // Then
    assertThat(count).isEqualTo(2);
  }
}
