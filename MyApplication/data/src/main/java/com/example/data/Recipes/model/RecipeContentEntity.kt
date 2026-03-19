package com.example.data.Recipes.model

import com.example.domain.Recipes.model.ContentBlock
import com.example.domain.Recipes.model.RecipeContent

data class RecipeContentEntity(
    val recipeId: Int,
    val ingredients: List<IngredientEntity>,
    val cookingSteps: List<ContentBlock>,
    val tips: String?
) {
    fun toDomain(): RecipeContent {
        return RecipeContent(
            recipeId = recipeId,
            ingredients = ingredients.map { it.toDomain() },
            cookingSteps = cookingSteps,
            tips = tips
        )
    }

}