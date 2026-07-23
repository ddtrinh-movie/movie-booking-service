package com.ddtrinh.movie_booking.payment.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class RefundChargeResponse {
    private UUID refundId;
    private UUID paymentId;
    private UUID bookingId;
    private BigDecimal amount;
    private Instant refundedAt;
}
