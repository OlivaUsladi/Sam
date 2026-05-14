package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RenameShoppingListRequestDto(
        @NotBlank
        @Size(max = 100)
        String name
) {
}
