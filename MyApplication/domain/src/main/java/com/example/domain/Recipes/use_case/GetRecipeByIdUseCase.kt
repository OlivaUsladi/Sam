package com.example.domain.Recipes.use_case

import com.example.domain.Recipes.model.Recipe
import com.example.domain.Recipes.repository.RecipeRepository

class GetRecipeByIdUseCase(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(recipeId: Int): Recipe? {
        return repository.getRecipeById(recipeId)
    }
}