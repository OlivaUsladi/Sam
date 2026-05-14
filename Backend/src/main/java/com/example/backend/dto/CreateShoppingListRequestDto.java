package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateShoppingListRequestDto(
        @NotBlank
        @Size(max = 100)
        String name
) {
}
