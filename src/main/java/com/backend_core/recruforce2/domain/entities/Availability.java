package com.backend_core.recruforce2.domain.entities;

import com.backend_core.recruforce2.domain.enums.WeekDay;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Represents a time slot availability for a recruiter or manager.
 * Used for matching interview scheduling with candidate availability.
 */
@Entity
@Table(name = "availabilities", indexes = {
  @Index(name = "idx_availability_user_id", columnList = "user_id"),
  @Index(name = "idx_availability_day", columnList = "day_of_week")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Availability {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /** The recruiter or manager this availability belongs to */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "start_date_time", nullable = false)
  private LocalDateTime startDateTime;

  @Column(name = "end_date_time", nullable = false)
  private LocalDateTime endDateTime;

  /** Whether this is a recurring weekly slot */
  @Column(name = "is_recurring", nullable = false)
  @Builder.Default
  private Boolean isRecurring = false;

  /** Day of the week for recurring slots */
  @Enumerated(EnumType.STRING)
  @Column(name = "day_of_week", length = 15)
  private WeekDay dayOfWeek;

  /** Whether this slot is still available (not already booked) */
  @Column(name = "is_available", nullable = false)
  @Builder.Default
  private Boolean isAvailable = true;

  // -------------------------------------------------------
  // Business methods
  // -------------------------------------------------------

  /** Adds a new availability slot */
  public void add() {
    this.isAvailable = true;
  }

  /** Updates the slot time range */
  public void update(LocalDateTime startDateTime, LocalDateTime endDateTime) {
    this.startDateTime = startDateTime;
    this.endDateTime = endDateTime;
  }

  /** Removes this availability slot */
  public void delete() {
    this.isAvailable = false;
  }

  /** Checks whether this slot is currently available */
  public Boolean checkAvailability() {
    return this.isAvailable &&
      this.endDateTime.isAfter(LocalDateTime.now());
  }
}
