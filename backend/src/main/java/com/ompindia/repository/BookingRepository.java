
package com.ompindia.repository;

import com.ompindia.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Optional<Booking> findByPixelNumber(Integer pixelNumber);

    boolean existsByPixelNumber(Integer pixelNumber);

    List<Booking> findByStatus(String status);
}
