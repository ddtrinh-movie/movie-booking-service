package com.ddtrinh.movie_booking.payment.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
public class PaymentClientConfig {

    private static final String INTERNAL_API_KEY_HEADER = "X-Internal-Api-Key";

    @Bean
    public RestClient paymentServiceRestClient(
            RestClient.Builder builder,
            @Value("${payment.service.base-url}") String baseUrl,
            @Value("${payment.service.connect-timeout-ms}") long connectTimeoutMs,
            @Value("${payment.service.read-timeout-ms}") long readTimeoutMs,
            @Value("${payment.service.api-key}") String apiKey) {

        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(connectTimeoutMs))
                .build();

        JdkClientHttpRequestFactory requestFactory =
                new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(Duration.ofMillis(readTimeoutMs));

        return builder
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .defaultHeader(INTERNAL_API_KEY_HEADER, apiKey)
                .build();
    }
}
