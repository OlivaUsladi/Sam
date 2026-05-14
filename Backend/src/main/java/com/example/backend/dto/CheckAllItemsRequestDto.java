package com.example.backend.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

public record CheckAllItemsRequestDto(
        @NotNull Boolean isChecked
) {
}
