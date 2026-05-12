package com.example.domain.Recipes.use_case


import com.example.domain.Recipes.model.GroceryItem
import com.example.domain.Recipes.model.RecipeIngredient
import com.example.domain.Recipes.model.ShoppingListItem
import com.example.domain.Recipes.repository.RecipeRepository

class AddItemsFromRecipeUseCase(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(listId: Int, recipeId: Int, recipeIngredientItems: List<RecipeIngredient>): List<ShoppingListItem> {
        return repository.addItemsFromRecipe(listId, recipeId, recipeIngredientItems)
    }
}