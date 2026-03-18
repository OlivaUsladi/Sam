package com.example.domain.Recipes.use_case

import com.example.domain.Recipes.repository.RecipeRepository

class GetLikesCountUseCase(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(recipeId: Int): Int {
        return repository.getLikesCount(recipeId)
    }
}