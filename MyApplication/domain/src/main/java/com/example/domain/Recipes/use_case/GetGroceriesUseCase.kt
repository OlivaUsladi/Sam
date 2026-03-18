package com.example.domain.Recipes.use_case

import com.example.domain.Recipes.model.Grocery
import com.example.domain.Recipes.repository.RecipeRepository

class GetGroceriesUseCase(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(): List<Grocery> {
        return repository.getGroceries()
    }
}