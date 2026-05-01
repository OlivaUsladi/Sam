package com.example.domain.Recipes.model

data class RecipeIngredient(
    val groceryItem: GroceryItem,
    val amount: Double,
    val unit: String
)