package com.example.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record AddItemsFromRecipeRequestDto(
        @NotNull Integer recipeId,
        @NotEmpty @Valid List<RecipeIngredientFromRecipeDto> ingredients
) {
}
