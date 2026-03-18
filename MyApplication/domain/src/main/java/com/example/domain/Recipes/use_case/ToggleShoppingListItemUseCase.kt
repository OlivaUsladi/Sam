package com.example.domain.Recipes.use_case

import com.example.domain.Recipes.model.ShoppingListItem
import com.example.domain.Recipes.repository.RecipeRepository

class ToggleShoppingListItemUseCase(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(itemId: Int, isChecked: Boolean): ShoppingListItem? {
        return repository.updateShoppingListItem(itemId, isChecked)
    }
}