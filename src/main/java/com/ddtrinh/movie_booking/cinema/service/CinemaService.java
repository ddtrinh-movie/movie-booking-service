package com.ddtrinh.movie_booking.cinema.service;

import com.ddtrinh.movie_booking.cinema.dto.CinemaRequest;
import com.ddtrinh.movie_booking.cinema.dto.CinemaResponse;
import com.ddtrinh.movie_booking.cinema.entity.Cinema;
import com.ddtrinh.movie_booking.cinema.repository.CinemaRepository;
import com.ddtrinh.movie_booking.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CinemaService {

    private final CinemaRepository cinemaRepository;

    public List<CinemaResponse> getAll() {
        return cinemaRepository.findAll().stream().map(CinemaResponse::new).toList();
    }

    public CinemaResponse getById(UUID id) {
        return new CinemaResponse(findCinemaOrThrow(id));
    }

    @Transactional
    public CinemaResponse create(CinemaRequest request) {
        Cinema cinema = new Cinema();
        cinema.setName(request.getName());
        cinema.setAddress(request.getAddress());
        cinemaRepository.save(cinema);
        return new CinemaResponse(cinema);
    }

    Cinema findCinemaOrThrow(UUID id) {
        return cinemaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cinema not found with id: " + id));
    }
}
