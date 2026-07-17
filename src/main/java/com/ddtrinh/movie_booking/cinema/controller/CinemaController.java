package com.ddtrinh.movie_booking.cinema.controller;

import com.ddtrinh.movie_booking.cinema.dto.CinemaRequest;
import com.ddtrinh.movie_booking.cinema.dto.CinemaResponse;
import com.ddtrinh.movie_booking.cinema.service.CinemaService;
import com.ddtrinh.movie_booking.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cinemas")
@RequiredArgsConstructor
public class CinemaController {

    private final CinemaService cinemaService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CinemaResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(cinemaService.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CinemaResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(cinemaService.getById(id)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<CinemaResponse>> create(@Valid @RequestBody CinemaRequest request) {
        CinemaResponse response = cinemaService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }
}
