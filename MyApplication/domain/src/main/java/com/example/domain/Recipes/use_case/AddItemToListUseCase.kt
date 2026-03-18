package com.example.domain.Recipes.use_case

import com.example.domain.Recipes.model.ShoppingListItem
import com.example.domain.Recipes.repository.RecipeRepository

class AddItemToListUseCase(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(listId: Int, item: ShoppingListItem): ShoppingListItem{
        return repository.addItemToList(listId, item)
    }
}