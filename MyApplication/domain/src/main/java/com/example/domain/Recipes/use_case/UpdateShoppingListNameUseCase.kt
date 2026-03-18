package com.example.domain.Recipes.use_case

import com.example.domain.Recipes.model.ShoppingList
import com.example.domain.Recipes.repository.RecipeRepository

class UpdateShoppingListNameUseCase(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(listId: Int, newName: String): ShoppingList? {
        return repository.updateShoppingListName(listId, newName.trim())
    }
}