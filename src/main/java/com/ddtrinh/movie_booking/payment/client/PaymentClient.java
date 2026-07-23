package com.ddtrinh.movie_booking.payment.client;

import com.ddtrinh.movie_booking.common.exception.PaymentServiceUnavailableException;
import com.ddtrinh.movie_booking.payment.dto.PaymentChargeRequest;
import com.ddtrinh.movie_booking.payment.dto.PaymentChargeResponse;
import com.ddtrinh.movie_booking.payment.dto.PaymentServiceEnvelope;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PaymentClient {

    private static final ParameterizedTypeReference<PaymentServiceEnvelope<PaymentChargeResponse>> RESPONSE_TYPE =
            new ParameterizedTypeReference<>() {
            };

    private final RestClient paymentServiceRestClient;

    public PaymentChargeResponse charge(UUID bookingId, BigDecimal amount) {
        PaymentChargeRequest request = new PaymentChargeRequest(bookingId, amount);
        try {
            PaymentServiceEnvelope<PaymentChargeResponse> envelope = paymentServiceRestClient.post()
                    .uri("/api/v1/payments")
                    .body(request)
                    .retrieve()
                    .body(RESPONSE_TYPE);
            assert envelope != null;
            return envelope.getData();
        } catch (RestClientException e) {
            throw new PaymentServiceUnavailableException(
                    "Could not reach payment service: " + e.getMessage());
        }
    }
}
