package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequestDto(
        @NotBlank String resetToken,
        @NotBlank
        @Size(min = 8, max = 100)
        @Pattern(
                regexp = "^(?=.*[A-Za-zА-Яа-я])(?=.*\\d).+$",
                message = "Пароль должен содержать хотя бы одну букву и одну цифру"
        )
        String newPassword
) {
}
