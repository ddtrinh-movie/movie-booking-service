package com.ddtrinh.movie_booking.movie.dto;

import com.ddtrinh.movie_booking.movie.entiy.Genre;
import com.ddtrinh.movie_booking.movie.entiy.Movie;
import com.ddtrinh.movie_booking.movie.entiy.MovieStatus;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
public class MovieResponse {

    private final UUID id;
    private final String title;
    private final String description;
    private final Integer durationMinutes;
    private final Genre genre;
    private final String posterUrl;
    private final LocalDate releaseDate;
    private final MovieStatus status;

    public MovieResponse(Movie movie) {
        this.id = movie.getId();
        this.title = movie.getTitle();
        this.description = movie.getDescription();
        this.durationMinutes = movie.getDurationMinutes();
        this.genre = movie.getGenre();
        this.posterUrl = movie.getPosterUrl();
        this.releaseDate = movie.getReleaseDate();
        this.status = movie.getStatus();
    }
}
