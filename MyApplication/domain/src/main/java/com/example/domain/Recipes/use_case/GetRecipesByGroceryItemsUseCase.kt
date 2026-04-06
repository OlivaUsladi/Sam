package com.example.domain.Recipes.use_case
import com.example.domain.Recipes.model.Recipe
import com.example.domain.Recipes.repository.RecipeRepository

class GetRecipesByGroceryItemsUseCase(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(groceryItemIds: List<Int>): List<Recipe> {
        return if (groceryItemIds.isEmpty()) {
            emptyList()
        } else {
            repository.getRecipesByGroceryItems(groceryItemIds)
        }
    }
}