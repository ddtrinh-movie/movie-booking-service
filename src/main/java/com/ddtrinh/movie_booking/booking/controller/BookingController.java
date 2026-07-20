package com.ddtrinh.movie_booking.booking.controller;

import com.ddtrinh.movie_booking.booking.dto.BookingRequest;
import com.ddtrinh.movie_booking.booking.dto.BookingResponse;
import com.ddtrinh.movie_booking.booking.service.BookingService;
import com.ddtrinh.movie_booking.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<ApiResponse<BookingResponse>> create(@AuthenticationPrincipal UUID userId,
                                                               @Valid @RequestBody BookingRequest request) {
        BookingResponse response = bookingService.create(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookingResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(bookingService.getById(id)));
    }
}
