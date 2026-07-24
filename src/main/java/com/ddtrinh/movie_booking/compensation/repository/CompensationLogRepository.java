package com.ddtrinh.movie_booking.compensation.repository;

import com.ddtrinh.movie_booking.compensation.entity.CompensationLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CompensationLogRepository extends JpaRepository<CompensationLog, UUID> {
}
