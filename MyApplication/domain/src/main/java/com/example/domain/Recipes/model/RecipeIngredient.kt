package com.example.domain.Recipes.model

data class RecipeIngredient(
    val ingredient: Ingredient,
    val amount: Double,
    val unit: String
)