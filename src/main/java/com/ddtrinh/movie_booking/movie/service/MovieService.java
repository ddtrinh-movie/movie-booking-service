package com.ddtrinh.movie_booking.movie.service;


import com.ddtrinh.movie_booking.common.exception.ConflictException;
import com.ddtrinh.movie_booking.common.exception.ResourceNotFoundException;
import com.ddtrinh.movie_booking.common.response.PageResponse;
import com.ddtrinh.movie_booking.movie.dto.MovieRequest;
import com.ddtrinh.movie_booking.movie.dto.MovieResponse;
import com.ddtrinh.movie_booking.movie.entiy.Genre;
import com.ddtrinh.movie_booking.movie.entiy.Movie;
import com.ddtrinh.movie_booking.movie.entiy.MovieStatus;
import com.ddtrinh.movie_booking.movie.repository.MovieRepository;
import com.ddtrinh.movie_booking.movie.repository.MovieSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;

    public PageResponse<MovieResponse> search(Genre genre, MovieStatus status, String keyword, Pageable pageable) {
        Specification<Movie> spec = Specification
                .where(MovieSpecification.excludeDeleted())
                .and(MovieSpecification.hasGenre(genre))
                .and(MovieSpecification.hasStatus(status))
                .and(MovieSpecification.titleContains(keyword));

        Page<MovieResponse> page = movieRepository.findAll(spec, pageable).map(MovieResponse::new);
        return new PageResponse<>(page);
    }

    public MovieResponse getById(UUID id) {
        return new MovieResponse(findMovieOrThrow(id));
    }

    @Transactional
    public MovieResponse create(MovieRequest request) {
        Movie movie = new Movie();
        applyRequest(movie, request);
        movieRepository.save(movie);
        return new MovieResponse(movie);
    }

    @Transactional
    public MovieResponse update(UUID id, MovieRequest request) {
        Movie movie = findMovieOrThrow(id);
        applyRequest(movie, request);
        movieRepository.save(movie);
        return new MovieResponse(movie);
    }

    @Transactional
    public void cancel(UUID id) {
        Movie movie = findMovieOrThrow(id);
        if (movie.isDeleted()) {
            throw new ConflictException("Movie is already deleted");
        }
        movie.setDeleted(true);
        movieRepository.save(movie);
    }

    private Movie findMovieOrThrow(UUID id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + id));
    }

    private void applyRequest(Movie movie, MovieRequest request) {
        movie.setTitle(request.getTitle());
        movie.setDescription(request.getDescription());
        movie.setDurationMinutes(request.getDurationMinutes());
        movie.setGenre(request.getGenre());
        movie.setPosterUrl(request.getPosterUrl());
        movie.setReleaseDate(request.getReleaseDate());
        movie.setStatus(request.getStatus());
    }
}
