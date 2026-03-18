package com.example.domain.Recipes.use_case

import com.example.domain.Recipes.model.Like
import com.example.domain.Recipes.repository.RecipeRepository

class AddLikeUseCase(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(userId: Int, recipeId: Int): Like {
        return repository.addLike(userId, recipeId)
    }
}