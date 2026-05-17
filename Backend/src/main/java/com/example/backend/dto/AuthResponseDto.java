package com.example.backend.dto;

public record AuthResponseDto(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn,
        MeResponseDto user
) {
}
