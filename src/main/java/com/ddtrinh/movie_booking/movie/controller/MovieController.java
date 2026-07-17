package com.ddtrinh.movie_booking.movie.controller;

import com.ddtrinh.movie_booking.common.response.ApiResponse;
import com.ddtrinh.movie_booking.common.response.PageResponse;
import com.ddtrinh.movie_booking.movie.dto.MovieRequest;
import com.ddtrinh.movie_booking.movie.dto.MovieResponse;
import com.ddtrinh.movie_booking.movie.entiy.Genre;
import com.ddtrinh.movie_booking.movie.entiy.MovieStatus;
import com.ddtrinh.movie_booking.movie.service.MovieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    public ResponseEntity<ApiResponse<PageResponse<MovieResponse>>> search(
            @RequestParam(required = false) Genre genre,
            @RequestParam(required = false) MovieStatus status,
            @RequestParam(required = false) String keyword,
            Pageable pageable) {
        PageResponse<MovieResponse> result = movieService.search(genre, status, keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MovieResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(movieService.getById(id)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<MovieResponse>> create (@Valid @RequestBody MovieRequest request) {
        MovieResponse response = movieService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MovieResponse>> update(@PathVariable UUID id, @Valid @RequestBody MovieRequest request)  {
        return ResponseEntity.ok(ApiResponse.success(movieService.update(id, request)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        movieService.cancel(id);
        return ResponseEntity.noContent().build();
    }
}
