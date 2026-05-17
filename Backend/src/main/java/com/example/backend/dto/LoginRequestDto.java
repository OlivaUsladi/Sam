package com.example.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequestDto(
        @NotBlank @Email @Size(max = 100) String email,
        @NotBlank @Size(min = 1, max = 100) String password
) {
}
