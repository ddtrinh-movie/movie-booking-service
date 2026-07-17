package com.ddtrinh.movie_booking.cinema.repository;

import com.ddtrinh.movie_booking.cinema.entity.Cinema;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CinemaRepository extends JpaRepository<Cinema, UUID> {
}
