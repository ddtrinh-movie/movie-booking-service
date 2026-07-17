package com.ddtrinh.movie_booking.cinema.dto;

import com.ddtrinh.movie_booking.cinema.entity.SeatType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class RowLayoutRequest {

    @NotBlank(message = "rowLabel must not be blank")
    private String rowLabel;

    @NotNull(message = "seatCount must not be null")
    @Positive(message = "seatCount must be a positive number")
    private Integer seatCount;

    private Map<SeatType, List<Integer>> seatTypeOverrides;
}
