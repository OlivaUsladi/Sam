package com.example.domain.Recipes.use_case

import com.example.domain.Recipes.model.Recipe
import com.example.domain.Recipes.repository.RecipeRepository

class GetRecipesUseCase(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(): List<Recipe> {
        return repository.getRecipes()
    }
}