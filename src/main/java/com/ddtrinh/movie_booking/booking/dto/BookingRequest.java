package com.ddtrinh.movie_booking.booking.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class BookingRequest {

    @NotNull(message = "showtimeId must not be null")
    private UUID showtimeId;

    @NotEmpty(message = "seatIds must not be empty")
    private List<UUID> seatIds;
}
