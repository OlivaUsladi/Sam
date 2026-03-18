package com.example.domain.Recipes.use_case

import com.example.domain.Recipes.model.Favourite
import com.example.domain.Recipes.repository.RecipeRepository

class AddToFavoritesUseCase(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(userId: Int, recipeId: Int): Favourite {
        return repository.addToFavorites(userId, recipeId)
    }
}