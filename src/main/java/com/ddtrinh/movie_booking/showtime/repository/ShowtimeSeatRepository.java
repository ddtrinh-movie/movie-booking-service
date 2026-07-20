package com.ddtrinh.movie_booking.showtime.repository;

import com.ddtrinh.movie_booking.showtime.entiy.ShowtimeSeat;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ShowtimeSeatRepository extends JpaRepository<ShowtimeSeat, UUID> {

    List<ShowtimeSeat> findAllByShowtimeId(UUID showtimeId);

    List<ShowtimeSeat> findAllByShowtimeIdAndIdIn(UUID showtimeId, List<UUID> ids);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT ss FROM ShowtimeSeat ss WHERE ss.showtime.id = :showtimeId AND ss.id IN :ids ORDER BY ss.id")
    List<ShowtimeSeat> findAllByShowtimeIdAndIdInForUpdate(@Param("showtimeId") UUID showtimeId, @Param("ids") List<UUID> ids);
}
