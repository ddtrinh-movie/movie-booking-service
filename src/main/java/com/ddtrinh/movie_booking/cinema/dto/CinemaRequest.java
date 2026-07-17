package com.ddtrinh.movie_booking.cinema.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CinemaRequest {

    @NotBlank(message = "name must not be blank")
    private String name;

    private String address;
}
