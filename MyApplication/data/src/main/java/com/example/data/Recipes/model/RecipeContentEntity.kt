package com.example.data.Recipes.model

import com.example.domain.Recipes.model.ContentBlock
import com.example.domain.Recipes.model.RecipeContent
import com.example.domain.Recipes.model.GroceryItem

data class RecipeContentEntity(
    val recipeId: Int,
    val cookingSteps: List<ContentBlock>,
    val tips: String?
)