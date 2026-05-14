package com.example.backend.dto;

import jakarta.validation.constraints.Size;

public record UpdateShoppingListItemRequestDto(
        @Size(max = 200)
        String description,
        Double quantity,
        @Size(max = 20)
        String unit,
        Boolean isChecked
) {
}
