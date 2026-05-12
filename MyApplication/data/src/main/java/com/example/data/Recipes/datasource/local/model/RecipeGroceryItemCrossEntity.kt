package com.example.data.Recipes.datasource.local.model

data class RecipeGroceryItemCrossEntity(
    val recipeId: Int,
    val groceryItemId: Int,
    val amount: Double,
    val unit: String
)