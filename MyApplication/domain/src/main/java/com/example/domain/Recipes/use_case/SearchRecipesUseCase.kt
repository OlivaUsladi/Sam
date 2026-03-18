package com.example.domain.Recipes.use_case

import com.example.domain.Recipes.model.Recipe
import com.example.domain.Recipes.repository.RecipeRepository

class SearchRecipesUseCase(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(query: String): List<Recipe> {
        return repository.searchRecipes(query)
    }
}