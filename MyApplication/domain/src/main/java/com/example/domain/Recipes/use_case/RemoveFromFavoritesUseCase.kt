package com.example.domain.Recipes.use_case

import com.example.domain.Recipes.repository.RecipeRepository

class RemoveFromFavoritesUseCase(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(userId: Int, recipeId: Int): Boolean {
        return repository.removeFromFavorites(userId, recipeId)
    }
}