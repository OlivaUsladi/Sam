package com.example.domain.Recipes.model

data class RecipeContent (
    val recipeId: Int,
    val ingredients: List<Ingredient>,
    val cookingSteps: List<ContentBlock>,
    //val videoUrl: String?, делаьб в конце видео рецепта или нет
    val tips: String?
)