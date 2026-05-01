package com.example.domain.Recipes.use_case

import com.example.domain.Recipes.model.GroceryItem
import com.example.domain.Recipes.repository.RecipeRepository

class GetGroceryItemByIdUseCase(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(groceryItemId: Int): GroceryItem? {
        return repository.getGroceryItemById(groceryItemId)
    }
}