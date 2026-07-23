package com.ddtrinh.movie_booking.payment.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class PaymentChargeResponse {
    private UUID paymentId;
    private UUID bookingId;
    private BigDecimal amount;
    private PaymentStatus status;
    private Instant processedAt;
}
