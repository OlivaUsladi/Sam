package com.example.domain.Recipes.use_case

import com.example.domain.Recipes.model.ShoppingList
import com.example.domain.Recipes.repository.RecipeRepository

class MergeShoppingListsUseCase(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(targetListId: Int, sourceListIds: List<Int>): ShoppingList? {
        return repository.mergeShoppingLists(targetListId, sourceListIds)
    }
}