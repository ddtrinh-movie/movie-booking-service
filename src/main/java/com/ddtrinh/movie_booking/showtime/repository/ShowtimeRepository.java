package com.ddtrinh.movie_booking.showtime.repository;

import com.ddtrinh.movie_booking.showtime.entiy.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.UUID;

public interface ShowtimeRepository extends JpaRepository<Showtime, UUID>, JpaSpecificationExecutor<Showtime> {

    @Query("""
            SELECT COUNT(s) > 0 FROM Showtime s
            WHERE s.room.id = :roomId
                AND s.id <> :excludeId
                AND s.startTime < :endTime
                AND s.endTime > :startTime
            """)
    boolean existsOverlapping(@Param("roomId") UUID roomId,
                              @Param("startTime") Instant startTime,
                              @Param("endTime") Instant endTime,
                              @Param("excludeId") UUID excludeId);
}
