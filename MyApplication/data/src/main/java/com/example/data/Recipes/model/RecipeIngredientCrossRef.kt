package com.example.data.Recipes.model

data class RecipeIngredientCrossRef(
    val recipeId: Int,
    val ingredientId: Int,
    val amount: Double,
    val unit: String
)