package com.example.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequestDto(
        @NotBlank @Size(min = 2, max = 50) String name,
        @NotBlank @Email @Size(max = 100) String email,
        @NotBlank
        @Size(min = 8, max = 100)
        @Pattern(
                regexp = "^(?=.*[A-Za-zА-Яа-я])(?=.*\\d).+$",
                message = "Пароль должен содержать хотя бы одну букву и одну цифру"
        )
        String password,
        @NotBlank String acceptedTermsVersion
) {
}
