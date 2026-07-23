package com.ddtrinh.movie_booking.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class PaymentChargeRequest {
    private UUID bookingId;
    private BigDecimal amount;
}
