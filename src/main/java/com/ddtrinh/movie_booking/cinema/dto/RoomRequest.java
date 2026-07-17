package com.ddtrinh.movie_booking.cinema.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class RoomRequest {

    @NotNull(message = "cinemaId must not be null")
    private UUID cinemaId;

    @NotBlank(message = "name must not be blank")
    private String name;

    @NotEmpty(message = "rows must not be empty")
    @Valid
    private List<RowLayoutRequest> rows;
}
