package com.ddtrinh.movie_booking.cinema.dto;

import com.ddtrinh.movie_booking.cinema.entity.Cinema;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CinemaResponse {

    private final UUID id;
    private final String name;
    private final String address;

    public CinemaResponse(Cinema cinema) {
        this.id = cinema.getId();
        this.name = cinema.getName();
        this.address = cinema.getAddress();
    }
}
