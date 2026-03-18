package com.example.domain.Recipes.use_case

import com.example.domain.Recipes.model.Recipe
import com.example.domain.Recipes.model.RecipeContent
import com.example.domain.Recipes.repository.RecipeRepository

class GetRecipeContentUseCase (
    private val repository: RecipeRepository){
    suspend operator fun invoke(recipeId: Int): RecipeContent? {
        return repository.getRecipeContent(recipeId)
    }
}