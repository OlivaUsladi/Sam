package com.example.domain.Recipes.use_case

import com.example.domain.Recipes.model.Category
import com.example.domain.Recipes.repository.RecipeRepository

class GetCategoriesUseCase(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(): List<Category> {
        return repository.getCategories()
    }
}