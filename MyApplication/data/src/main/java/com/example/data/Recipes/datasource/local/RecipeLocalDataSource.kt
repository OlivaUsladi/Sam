package com.example.data.Recipes.datasource.local

import com.example.data.Recipes.model.*

interface RecipeLocalDataSource {
    suspend fun getRecipes(): List<RecipeEntity>
    suspend fun getRecipeById(recipeId: Int): RecipeEntity?
    suspend fun getRecipesByCategory(categoryId: Int): List<RecipeEntity>
    suspend fun getRecipesByGrocery(groceryId: Int): List<RecipeEntity>
    suspend fun searchRecipes(query: String): List<RecipeEntity>

    suspend fun getCategories(): List<CategoryEntity>
    suspend fun getCategoryById(categoryId: Int): CategoryEntity?

    suspend fun getGroceries(): List<GroceryEntity>
    suspend fun getGroceryById(groceryId: Int): GroceryEntity?

    suspend fun getGroceryItems(): List<GroceryItemEntity>
    suspend fun getGroceryItemById(groceryItemId: Int): GroceryItemEntity?
    suspend fun getRecipeGroceryItemIds(recipeId: Int): List<Int>
    suspend fun getAllRecipeGroceryItemCross(): List<RecipeGroceryItemCrossEntity>
    suspend fun getRecipeGroceryItemsCrossRef(recipeId: Int): List<RecipeGroceryItemCrossEntity>

    suspend fun getRecipeCategoryIds(recipeId: Int): List<Int>
    suspend fun getRecipeGroceryIds(recipeId: Int): List<Int>

    suspend fun getRecipeContent(recipeId: Int): RecipeContentEntity?

    suspend fun getFavorites(userId: Int): List<FavoriteEntity>
    suspend fun addFavorite(userId: Int, recipeId: Int): FavoriteEntity
    suspend fun removeFavorite(userId: Int, recipeId: Int): Boolean
    suspend fun isFavorite(userId: Int, recipeId: Int): Boolean

    suspend fun getLikes(recipeId: Int): List<LikeEntity>
    suspend fun getUserLikes(userId: Int): List<LikeEntity>
    suspend fun addLike(userId: Int, recipeId: Int): LikeEntity
    suspend fun removeLike(userId: Int, recipeId: Int): Boolean
    suspend fun isLiked(userId: Int, recipeId: Int): Boolean
    suspend fun getLikesCount(recipeId: Int): Int


    suspend fun getRecipesByGroceryItems(groceryItemIds: List<Int>): List<RecipeEntity>
    suspend fun getRecipesByExactGroceryItems(groceryItemIds: List<Int>): List<RecipeEntity>
}