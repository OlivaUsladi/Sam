package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RecipeIngredientFromRecipeDto(
        @NotBlank
        @Size(max = 200)
        String name,
        Double amount,
        @Size(max = 20) String unit
) {
}
