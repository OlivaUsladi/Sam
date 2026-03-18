package com.example.domain.Recipes.use_case

import com.example.domain.Recipes.repository.RecipeRepository

class DeleteShoppingListUseCase(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(listId: Int): Boolean {
        return repository.deleteShoppingList(listId)
    }
}