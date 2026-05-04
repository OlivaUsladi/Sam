package com.example.data.Recipes.remote.dto

import java.math.BigDecimal

data class RecipeIngredientDto(
    val groceryItem: GroceryItemDto,
    val amount: BigDecimal,
    val unit: String
)