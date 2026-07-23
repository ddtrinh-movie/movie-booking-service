package com.ddtrinh.movie_booking.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class RefundChargeRequest {
    private UUID paymentId;
}
