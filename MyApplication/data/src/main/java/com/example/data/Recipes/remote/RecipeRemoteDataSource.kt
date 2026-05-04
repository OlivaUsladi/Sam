package com.example.data.Recipes.remote

import com.example.data.Recipes.remote.api.RecipeApiService
import com.example.data.Recipes.remote.dto.*

class RecipeRemoteDataSource(
    private val apiService: RecipeApiService
) {

    //Без авторизации

    suspend fun getAllRecipesPublic(): List<RecipeResponseDto> {
        return try {
            apiService.getAllRecipesPublic()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getRecipeByIdPublic(id: Int): RecipeDetailResponseDto? {
        return try {
            apiService.getRecipeByIdPublic(id)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun searchRecipesPublic(query: String): List<RecipeResponseDto> {
        return try {
            apiService.searchRecipesPublic(query)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getAllCategories(): List<CategoryDto> {
        return try {
            apiService.getAllCategories()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getAllGroceryItems(): List<GroceryItemDto> {
        return try {
            apiService.getAllGroceryItems()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getRecipesByGroceryItemsPublic(groceryItemIds: List<Int>): List<RecipeResponseDto> {
        return try {
            apiService.getRecipesByGroceryItemsPublic(groceryItemIds)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getRecipesByExactGroceryItemsPublic(groceryItemIds: List<Int>): List<RecipeResponseDto> {
        return try {
            apiService.getRecipesByExactGroceryItemsPublic(groceryItemIds)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getRecipesByCategoryPublic(categoryId: Int): List<RecipeResponseDto> {
        return try {
            apiService.getRecipesByCategoryPublic(categoryId)
        } catch (e: Exception) {
            emptyList()
        }
    }


    //С авторизацией

    suspend fun getAllRecipesForUser(userId: Int): List<RecipeResponseDto> {
        return try {
            apiService.getAllRecipesForUser(userId)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getRecipeByIdForUser(id: Int, userId: Int): RecipeDetailResponseDto? {
        return try {
            apiService.getRecipeByIdForUser(id, userId)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun searchRecipesForUser(query: String, userId: Int): List<RecipeResponseDto> {
        return try {
            apiService.searchRecipesForUser(query, userId)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getRecipesByCategoryForUser(categoryId: Int, userId: Int): List<RecipeResponseDto> {
        return try {
            apiService.getRecipesByCategoryForUser(categoryId, userId)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getFavouriteRecipes(userId: Int): List<RecipeResponseDto> {
        return try {
            apiService.getFavouriteRecipes(userId)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addLike(recipeId: Int, userId: Int): Boolean {
        return try {
            val response = apiService.addLike(recipeId, userId)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    suspend fun removeLike(recipeId: Int, userId: Int): Boolean {
        return try {
            val response = apiService.removeLike(recipeId, userId)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    suspend fun addToFavourites(recipeId: Int, userId: Int): Boolean {
        return try {
            val response = apiService.addToFavourites(recipeId, userId)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    suspend fun removeFromFavourites(recipeId: Int, userId: Int): Boolean {
        return try {
            val response = apiService.removeFromFavourites(recipeId, userId)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }


    suspend fun getRecipesByGroceryItemsForUser(groceryItemIds: List<Int>, userId: Int): List<RecipeResponseDto> {
        return try {
            apiService.getRecipesByGroceryItemsForUser(groceryItemIds, userId)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getRecipesByExactGroceryItemsForUser(groceryItemIds: List<Int>, userId: Int): List<RecipeResponseDto> {
        return try {
            apiService.getRecipesByExactGroceryItemsForUser(groceryItemIds, userId)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getAllGroceries(): List<GroceryDto> {
        return try {
            apiService.getAllGroceries()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
