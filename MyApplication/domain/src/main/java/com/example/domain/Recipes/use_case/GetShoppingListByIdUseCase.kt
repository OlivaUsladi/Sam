package com.example.domain.Recipes.use_case

import com.example.domain.Recipes.model.ShoppingList
import com.example.domain.Recipes.repository.RecipeRepository

class GetShoppingListByIdUseCase (
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(listId: Int): ShoppingList? {
        return repository.getShoppingListById(listId)
    }
}