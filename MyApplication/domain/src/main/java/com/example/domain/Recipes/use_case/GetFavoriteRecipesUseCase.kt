package com.example.domain.Recipes.use_case

import com.example.domain.Recipes.model.Recipe
import com.example.domain.Recipes.repository.RecipeRepository

class GetFavoriteRecipesUseCase(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(userId: Int): List<Recipe> {
        return repository.getFavoriteRecipes(userId)
    }
}