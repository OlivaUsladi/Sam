package com.example.domain.Recipes.model

//Хранит данные для конкретного рецепта
data class RecipeGroceryItem(
    val recipeId: Int,
    val groceryItemId: Int,
    val amount: Double,
    val unit: String
)