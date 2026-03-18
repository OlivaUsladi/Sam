package com.example.domain.Recipes.use_case

import com.example.domain.Recipes.repository.RecipeRepository

class RemoveShoppingListItemUseCase(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(itemId: Int): Boolean {
        return repository.removeShoppingListItem(itemId)
    }
}