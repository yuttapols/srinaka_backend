package com.srinaka.booking.repository;

import com.srinaka.booking.entity.Booking;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @EntityGraph(attributePaths = {"customer", "service", "employee", "promotion"})
    List<Booking> findAllByOrderByScheduledAtDesc();

    @EntityGraph(attributePaths = {"customer", "service", "employee", "promotion"})
    List<Booking> findByEmployee_IdOrderByScheduledAtDesc(Long employeeId);

    @EntityGraph(attributePaths = {"customer", "service", "employee", "promotion"})
    List<Booking> findByCustomer_IdOrderByScheduledAtDesc(Long customerId);

    @EntityGraph(attributePaths = {"customer", "service", "employee", "promotion"})
    Optional<Booking> findById(Long id);
}
