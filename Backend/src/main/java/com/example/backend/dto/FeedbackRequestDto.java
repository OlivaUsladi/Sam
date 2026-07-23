package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FeedbackRequestDto(
        @NotBlank @Size(max = 100) String subject,
        @NotBlank @Size(max = 2000) String message
) {
}
