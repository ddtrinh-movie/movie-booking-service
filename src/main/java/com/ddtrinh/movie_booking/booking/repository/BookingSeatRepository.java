package com.ddtrinh.movie_booking.booking.repository;

import com.ddtrinh.movie_booking.booking.entiy.BookingSeat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BookingSeatRepository extends JpaRepository<BookingSeat, UUID> {

    List<BookingSeat> findAllByBookingId(UUID bookingId);
}
