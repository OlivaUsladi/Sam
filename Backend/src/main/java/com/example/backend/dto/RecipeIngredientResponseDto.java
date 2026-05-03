package com.example.backend.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class RecipeIngredientResponseDto {
    private GroceryItemResponseDto groceryItem;
    private BigDecimal amount;
    private String unit;
}