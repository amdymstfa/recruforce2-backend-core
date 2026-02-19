package com.backend_core.recruforce2.repository;

import com.backend_core.recruforce2.domain.entities.Availability;
import com.backend_core.recruforce2.domain.enums.WeekDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * JPA repository for {@link Availability} entity.
 * Provides access to the {@code availabilities} table in PostgreSQL.
 */
@Repository
public interface InterviewSlotRepository extends JpaRepository<Availability, Long> {

  /** Finds all availability slots for a specific user */
  List<Availability> findByUserId(Long userId);

  /** Finds all available (not booked) slots for a specific user */
  List<Availability> findByUserIdAndIsAvailableTrue(Long userId);

  /** Finds recurring slots for a specific user on a given day of week */
  List<Availability> findByUserIdAndIsRecurringTrueAndDayOfWeek(Long userId, WeekDay dayOfWeek);

  /** Finds available slots for a user within a specific time range */
  @Query("SELECT a FROM Availability a WHERE " +
    "a.user.id = :userId AND " +
    "a.isAvailable = true AND " +
    "a.startDateTime >= :from AND " +
    "a.endDateTime <= :to " +
    "ORDER BY a.startDateTime ASC")
  List<Availability> findAvailableSlotsByUserAndRange(@Param("userId") Long userId,
                                                      @Param("from") LocalDateTime from,
                                                      @Param("to") LocalDateTime to);

  /** Finds all available slots across all recruiters/managers within a time range */
  @Query("SELECT a FROM Availability a WHERE " +
    "a.isAvailable = true AND " +
    "a.startDateTime >= :from AND " +
    "a.endDateTime <= :to " +
    "ORDER BY a.startDateTime ASC")
  List<Availability> findAllAvailableSlotsInRange(@Param("from") LocalDateTime from,
                                                  @Param("to") LocalDateTime to);

  /** Finds overlapping slots for a user (to prevent double-booking) */
  @Query("SELECT a FROM Availability a WHERE " +
    "a.user.id = :userId AND " +
    "a.startDateTime < :end AND " +
    "a.endDateTime > :start")
  List<Availability> findOverlapping(@Param("userId") Long userId,
                                     @Param("start") LocalDateTime start,
                                     @Param("end") LocalDateTime end);

  /** Counts available slots per user */
  long countByUserIdAndIsAvailableTrue(Long userId);
}
