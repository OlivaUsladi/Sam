package com.example.backend.dto;

public record ForgotPasswordResponseDto(
        String message,
        String resetToken
) {
}
