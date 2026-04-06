package com.example.domain.Recipes.use_case

import com.example.domain.Recipes.model.GroceryItem
import com.example.domain.Recipes.repository.RecipeRepository

class GetGroceryItemsUseCase(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(): List<GroceryItem> {
        return repository.getGroceryItems()
    }
}