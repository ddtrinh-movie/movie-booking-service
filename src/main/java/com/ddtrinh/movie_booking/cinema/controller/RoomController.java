package com.ddtrinh.movie_booking.cinema.controller;

import com.ddtrinh.movie_booking.cinema.dto.RoomRequest;
import com.ddtrinh.movie_booking.cinema.dto.RoomResponse;
import com.ddtrinh.movie_booking.cinema.dto.SeatResponse;
import com.ddtrinh.movie_booking.cinema.service.RoomService;
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
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<RoomResponse>>> getAllByCinema(@RequestParam UUID cinemaId) {
        return ResponseEntity.ok(ApiResponse.success(roomService.getAllByCinema(cinemaId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RoomResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(roomService.getById(id)));
    }

    @GetMapping("/{id}/seats")
    public ResponseEntity<ApiResponse<List<SeatResponse>>> getSeats(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(roomService.getSeats(id)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<RoomResponse>> create(@Valid @RequestBody RoomRequest request) {
        RoomResponse response = roomService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }
}
