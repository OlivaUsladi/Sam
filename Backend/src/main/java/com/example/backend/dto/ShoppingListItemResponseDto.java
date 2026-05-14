package com.example.backend.dto;

public record ShoppingListItemResponseDto(
        Integer id,
        String description,
        Boolean isChecked,
        Double quantity,
        String unit
) {
}
