package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddShoppingListItemRequestDto(
        @NotBlank
        @Size(max = 200)
        String description,
        Double quantity,
        @Size(max = 20)
        String unit
) {
}
