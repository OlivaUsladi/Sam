package com.example.domain.Recipes.use_case

import com.example.domain.Recipes.model.Recipe
import com.example.domain.Recipes.repository.RecipeRepository

class GetRecipesByCategoryUseCase(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(categoryId: Int): List<Recipe> {
        return repository.getRecipesByCategory(categoryId)
    }
}