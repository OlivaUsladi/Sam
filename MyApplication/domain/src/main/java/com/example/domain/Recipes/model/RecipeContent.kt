package com.example.domain.Recipes.model

data class RecipeContent(
    val recipeId: Int,
    val recipeIngredients: List<RecipeGroceryItem>,
    val cookingSteps: List<ContentBlock>,
    val tips: String?
)