package com.example.domain.Recipes.model

data class RecipeContent(
    val recipeId: Int,
    val ingredients: List<RecipeIngredient>,
    val cookingSteps: List<ContentBlock>,
    val tips: String?
)