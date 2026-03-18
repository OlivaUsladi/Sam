package com.example.domain.Recipes.use_case

import com.example.domain.Recipes.model.ShoppingListItem
import com.example.domain.Recipes.repository.RecipeRepository

class GetShoppingListItemsUseCase (
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(listId: Int): List<ShoppingListItem> {
        return repository.getShoppingListItems(listId)
    }
}