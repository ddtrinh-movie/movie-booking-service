package com.ddtrinh.movie_booking.payment.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentServiceEnvelope<T> {
    private boolean success;
    private T data;
    private String message;
    private String errorCode;
    private String timestamp;
}
