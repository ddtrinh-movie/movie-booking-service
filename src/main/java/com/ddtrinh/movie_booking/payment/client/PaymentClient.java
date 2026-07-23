package com.ddtrinh.movie_booking.payment.client;

import com.ddtrinh.movie_booking.common.exception.ConflictException;
import com.ddtrinh.movie_booking.common.exception.PaymentServiceUnavailableException;
import com.ddtrinh.movie_booking.common.exception.ResourceNotFoundException;
import com.ddtrinh.movie_booking.payment.dto.*;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PaymentClient {

    private static final ParameterizedTypeReference<PaymentServiceEnvelope<PaymentChargeResponse>> CHARGE_RESPONSE_TYPE =
            new ParameterizedTypeReference<>() {
            };

    private static final ParameterizedTypeReference<PaymentServiceEnvelope<RefundChargeResponse>> REFUND_RESPONSE_TYPE =
            new ParameterizedTypeReference<>() {
            };

    private final RestClient paymentServiceRestClient;

    @Retry(name = "paymentService")
    @CircuitBreaker(name = "paymentService", fallbackMethod = "chargeFallback")
    public PaymentChargeResponse charge(UUID bookingId, BigDecimal amount) {
        PaymentChargeRequest request = new PaymentChargeRequest(bookingId, amount);
        try {
            PaymentServiceEnvelope<PaymentChargeResponse> envelope = paymentServiceRestClient.post()
                    .uri("/api/v1/payments")
                    .body(request)
                    .retrieve()
                    .body(CHARGE_RESPONSE_TYPE);
            if (envelope == null || envelope.getData() == null) {
                throw new PaymentServiceUnavailableException("Payment service returned an empty response");
            }
            return envelope.getData();
        } catch (RestClientException e) {
            throw new PaymentServiceUnavailableException(
                    "Could not reach payment service: " + e.getMessage());
        }
    }

    private PaymentChargeResponse chargeFallback(UUID bookingId, BigDecimal amount, Throwable t) {
        throw new PaymentServiceUnavailableException(
                "Payment service is unavailable after retries/circuit breaker: " + t.getMessage());
    }

    @Retry(name = "paymentService")
    @CircuitBreaker(name = "paymentService", fallbackMethod = "refundFallback")
    public RefundChargeResponse refund(UUID paymentId) {
        RefundChargeRequest request = new RefundChargeRequest(paymentId);
        try {
            PaymentServiceEnvelope<RefundChargeResponse> envelope = paymentServiceRestClient.post()
                    .uri("/api/v1/refunds")
                    .body(request)
                    .retrieve()
                    .body(REFUND_RESPONSE_TYPE);
            if (envelope == null || envelope.getData() == null) {
                throw new PaymentServiceUnavailableException("Payment service returned an empty response");
            }
            return envelope.getData();
        } catch (HttpClientErrorException.Conflict e) {
            throw new ConflictException("This payment has already been refunded (or was never successful)");
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResourceNotFoundException("Payment not found for refund: " + paymentId);
        } catch (RestClientException e) {
            throw new PaymentServiceUnavailableException(
                    "Could not reach payment service: " + e.getMessage());
        }
    }

    private RefundChargeResponse refundFallback(UUID paymentId, Throwable t) {
        throw new PaymentServiceUnavailableException(
                "Payment service is unavailable after retries/circuit breaker: " + t.getMessage());
    }
}
