package com.ddtrinh.movie_booking.security.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email is not a valid format")
    private String email;

    @NotBlank(message = "Password must not be blank")
    private String password;
}
