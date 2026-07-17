package com.ddtrinh.movie_booking.showtime.service;

import com.ddtrinh.movie_booking.cinema.entity.Room;
import com.ddtrinh.movie_booking.cinema.entity.Seat;
import com.ddtrinh.movie_booking.cinema.repository.RoomRepository;
import com.ddtrinh.movie_booking.cinema.repository.SeatRepository;
import com.ddtrinh.movie_booking.common.exception.ConflictException;
import com.ddtrinh.movie_booking.common.exception.ResourceNotFoundException;
import com.ddtrinh.movie_booking.common.response.PageResponse;
import com.ddtrinh.movie_booking.movie.entiy.Movie;
import com.ddtrinh.movie_booking.movie.repository.MovieRepository;
import com.ddtrinh.movie_booking.showtime.dto.ShowtimeRequest;
import com.ddtrinh.movie_booking.showtime.dto.ShowtimeResponse;
import com.ddtrinh.movie_booking.showtime.dto.ShowtimeSeatResponse;
import com.ddtrinh.movie_booking.showtime.entiy.AudioType;
import com.ddtrinh.movie_booking.showtime.entiy.Showtime;
import com.ddtrinh.movie_booking.showtime.entiy.ShowtimeSeat;
import com.ddtrinh.movie_booking.showtime.entiy.SubtitleType;
import com.ddtrinh.movie_booking.showtime.repository.ShowtimeRepository;
import com.ddtrinh.movie_booking.showtime.repository.ShowtimeSeatRepository;
import com.ddtrinh.movie_booking.showtime.repository.ShowtimeSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShowtimeService {

    private static final UUID NIL_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    private final ShowtimeRepository showtimeRepository;
    private final MovieRepository movieRepository;
    private final RoomRepository roomRepository;
    private final SeatRepository seatRepository;
    private final ShowtimeSeatRepository showtimeSeatRepository;

    public PageResponse<ShowtimeResponse> search(UUID movieId, UUID cinemaId, Instant from, Instant to, AudioType audioType, SubtitleType subtitleType, Pageable pageable) {
        Specification<Showtime> spec = Specification
                .where(ShowtimeSpecification.excludeCancelled())
                .and(ShowtimeSpecification.hasMovieId(movieId))
                .and(ShowtimeSpecification.hasCinemaId(cinemaId))
                .and(ShowtimeSpecification.startsBetween(from, to))
                .and(ShowtimeSpecification.hasAudioType(audioType))
                .and(ShowtimeSpecification.hasSubtitleType(subtitleType));

        Page<ShowtimeResponse> page = showtimeRepository.findAll(spec, pageable).map(ShowtimeResponse::new);
        return new PageResponse<>(page);
    }

    public ShowtimeResponse getById(UUID id) {
        return new ShowtimeResponse(findShowTimeOrThrow(id));
    }

    public List<ShowtimeSeatResponse> getSeats(UUID showtimeId) {
        findShowTimeOrThrow(showtimeId);
        return showtimeSeatRepository.findAllByShowtimeId(showtimeId).stream()
                .map(ShowtimeSeatResponse::new)
                .toList();
    }

    @Transactional
    public ShowtimeResponse create(ShowtimeRequest request) {
        validateTimeRange(request);

        Movie movie = movieRepository.findById(request.getMovieId()).orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + request.getMovieId()));

        Room room = roomRepository.findById(request.getRoomId()).orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + request.getRoomId()));

        checkNoOverlap(request, NIL_UUID);

        Showtime showtime = new Showtime();
        populateShowtime(request, movie, room, showtime);
        showtimeRepository.save(showtime);

        generateShowtimeSeats(showtime, room);

        return new ShowtimeResponse(showtime);
    }

    @Transactional
    public ShowtimeResponse update(UUID id, ShowtimeRequest request) {
        validateTimeRange(request);

        Showtime showtime = findShowTimeOrThrow(id);

        Movie movie = movieRepository.findById(request.getMovieId()).orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + request.getMovieId()));
        Room room = roomRepository.findById(request.getRoomId()).orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + request.getRoomId()));

        checkNoOverlap(request, id);

        populateShowtime(request, movie, room, showtime);
        showtimeRepository.save(showtime);

        return new ShowtimeResponse(showtime);
    }

    @Transactional
    public void cancel(UUID id) {
        Showtime showtime = findShowTimeOrThrow(id);
        if (showtime.isDeleted()) {
            throw new ConflictException("Showtime is already cancelled");
        }
        showtime.setDeleted(true);
        showtimeRepository.save(showtime);
    }

    private void generateShowtimeSeats(Showtime showtime, Room room) {
        List<Seat> seats = seatRepository.findAllByRoomId(room.getId());
        for(Seat seat : seats) {
            ShowtimeSeat showtimeSeat = new ShowtimeSeat();
            showtimeSeat.setShowtime(showtime);
            showtimeSeat.setSeat(seat);
            showtimeSeatRepository.save(showtimeSeat);
        }
    }

    private void validateTimeRange(ShowtimeRequest request) {
        if (request.getStartTime().isBefore(Instant.now())) {
            throw new ConflictException("startTime must not be in the past");
        }
        if (!request.getEndTime().isAfter(request.getStartTime())) {
            throw new ConflictException("endTime must be after startTime");
        }
    }

    private void checkNoOverlap(ShowtimeRequest request, UUID excludeId) {
        boolean overlapping = showtimeRepository.existsOverlapping(request.getRoomId(), request.getStartTime(), request.getEndTime(), excludeId);
        if (overlapping) {
            throw new ConflictException("This room already has another showtime overlapping this time range");
        }
    }

    private Showtime findShowTimeOrThrow(UUID id) {
        return showtimeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Showtime not found with id: " + id));
    }

    private void populateShowtime(ShowtimeRequest request, Movie movie, Room room, Showtime showtime) {
        showtime.setMovie(movie);
        showtime.setRoom(room);
        showtime.setStartTime(request.getStartTime());
        showtime.setEndTime(request.getEndTime());
        showtime.setPrice(request.getPrice());
        showtime.setAudioType(request.getAudioType());
        showtime.setSubtitleType(request.getSubtitleType());
    }
}
