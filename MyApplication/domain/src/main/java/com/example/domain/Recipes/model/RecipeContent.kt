package com.example.domain.Recipes.model

data class RecipeContent(
    val recipeId: Int,
    val groceryItems: List<GroceryItem>,
    val cookingSteps: List<ContentBlock>,
    val tips: String?
)