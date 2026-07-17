package com.ddtrinh.movie_booking.showtime.repository;

import com.ddtrinh.movie_booking.showtime.entiy.ShowtimeSeat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ShowtimeSeatRepository extends JpaRepository<ShowtimeSeat, UUID> {

    List<ShowtimeSeat> findAllByShowtimeId(UUID showtimeId);

    List<ShowtimeSeat> findAllByShowtimeIdAndIdIn(UUID showtimeId, List<UUID> ids);
}
