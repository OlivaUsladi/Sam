package com.example.domain.Recipes.use_case

import com.example.domain.Recipes.model.ShoppingList
import com.example.domain.Recipes.repository.RecipeRepository

class GetShoppingListsUseCase(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(userId: Int): List<ShoppingList> {
        return repository.getShoppingLists(userId)
    }
}