package com.example.domain.Recipes.use_case

import com.example.domain.Recipes.model.ShoppingListItem
import com.example.domain.Recipes.repository.RecipeRepository

class UpdateShoppingListItemDetailsUseCase(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(itemId: Int, description: String, quantity: Double?, unit: String?): ShoppingListItem?{
        return repository.updateShoppingListItemDetails(itemId, description, quantity, unit)
    }
}