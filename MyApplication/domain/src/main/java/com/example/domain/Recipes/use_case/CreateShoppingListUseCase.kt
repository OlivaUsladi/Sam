package com.example.domain.Recipes.use_case

import com.example.domain.Recipes.model.ShoppingList
import com.example.domain.Recipes.repository.RecipeRepository

class CreateShoppingListUseCase(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(userId: Int, name: String, recipeId: Int? = null): ShoppingList {
        return repository.createShoppingList(userId, name, recipeId)
    }
}