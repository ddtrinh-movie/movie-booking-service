package com.ddtrinh.movie_booking.showtime.controller;

import com.ddtrinh.movie_booking.common.response.ApiResponse;
import com.ddtrinh.movie_booking.common.response.PageResponse;
import com.ddtrinh.movie_booking.showtime.dto.ShowtimeRequest;
import com.ddtrinh.movie_booking.showtime.dto.ShowtimeResponse;
import com.ddtrinh.movie_booking.showtime.entiy.AudioType;
import com.ddtrinh.movie_booking.showtime.entiy.SubtitleType;
import com.ddtrinh.movie_booking.showtime.service.ShowtimeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/showtimes")
@RequiredArgsConstructor
public class ShowtimeController {

    private final ShowtimeService showtimeService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ShowtimeResponse>>> search(
            @RequestParam(required = false) UUID movieId,
            @RequestParam(required = false) UUID cinemaId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @RequestParam(required = false) AudioType audioType,
            @RequestParam(required = false) SubtitleType subtitleType,
            Pageable pageable
    ) {
        PageResponse<ShowtimeResponse> result =
                showtimeService.search(movieId, cinemaId, from, to, audioType, subtitleType, pageable);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ShowtimeResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(showtimeService.getById(id)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<ShowtimeResponse>> create(@Valid @RequestBody ShowtimeRequest request) {
        ShowtimeResponse response = showtimeService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ShowtimeResponse>> update(@PathVariable UUID id,
                                                                @Valid @RequestBody ShowtimeRequest request) {
        return ResponseEntity.ok(ApiResponse.success(showtimeService.update(id, request)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        showtimeService.cancel(id);
        return ResponseEntity.noContent().build();
    }
}
