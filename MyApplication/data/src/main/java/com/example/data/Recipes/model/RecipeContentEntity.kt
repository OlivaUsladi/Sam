package com.example.data.Recipes.model

import com.example.domain.Recipes.model.ContentBlock
import com.example.domain.Recipes.model.Ingredient
import com.example.domain.Recipes.model.RecipeContent
import com.example.domain.Recipes.model.RecipeIngredient

data class RecipeContentEntity(
    val recipeId: Int,
    val cookingSteps: List<ContentBlock>,
    val tips: String?
) {
    fun toDomain(ingredients: List<RecipeIngredient>): RecipeContent {
        return RecipeContent(
            recipeId = recipeId,
            ingredients = ingredients,
            cookingSteps = cookingSteps,
            tips = tips
        )
    }
}