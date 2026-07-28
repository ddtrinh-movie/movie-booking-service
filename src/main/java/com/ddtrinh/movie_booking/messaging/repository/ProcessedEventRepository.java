package com.ddtrinh.movie_booking.messaging.repository;

import com.ddtrinh.movie_booking.messaging.entity.ProcessedEvent;
import com.ddtrinh.movie_booking.messaging.entity.ProcessedEventId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;

public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, ProcessedEventId> {
    void deleteAllByProcessedAtBefore(Instant processedAtBefore);
}
