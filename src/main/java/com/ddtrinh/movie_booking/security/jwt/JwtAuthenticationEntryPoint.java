package com.ddtrinh.movie_booking.security.jwt;

import com.ddtrinh.movie_booking.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Handles requests to protected endpoints with a missing/invalid token.
 * Needs its own class because this error happens in the filter chain (before DispatcherServlet),
 * so GlobalExceptionHandler (@RestControllerAdvice) CANNOT catch it — the response
 * must be written manually in the correct ApiResponse format here.
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(@NonNull HttpServletRequest request, HttpServletResponse response, @NonNull AuthenticationException authException) throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ApiResponse<Void> body = ApiResponse.error("Not authenticated or invalid token", "UNAUTHORIZED");
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
