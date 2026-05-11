package com.example.data.Recipes.datasource.remote.api

import com.example.data.Recipes.datasource.remote.dto.CategoryDto
import com.example.data.Recipes.datasource.remote.dto.GroceryDto
import com.example.data.Recipes.datasource.remote.dto.GroceryItemDto
import com.example.data.Recipes.datasource.remote.dto.RecipeDetailResponseDto
import com.example.data.Recipes.datasource.remote.dto.RecipeResponseDto
import retrofit2.Response
import retrofit2.http.*

interface RecipeApiService {

    //Это без авторизации

    @GET("/api/recipes/public")
    suspend fun getAllRecipesPublic(): List<RecipeResponseDto>

    @GET("/api/recipes/public/{id}")
    suspend fun getRecipeByIdPublic(@Path("id") id: Int): RecipeDetailResponseDto

    @GET("/api/recipes/public/search")
    suspend fun searchRecipesPublic(@Query("query") query: String): List<RecipeResponseDto>

    @GET("/api/categories")
    suspend fun getAllCategories(): List<CategoryDto>

    @GET("/api/grocery-items")
    suspend fun getAllGroceryItems(): List<GroceryItemDto>

    @POST("/api/recipes/public/by-grocery-items")
    suspend fun getRecipesByGroceryItemsPublic(@Body groceryItemIds: List<Int>): List<RecipeResponseDto>

    @POST("/api/recipes/public/by-exact-grocery-items")
    suspend fun getRecipesByExactGroceryItemsPublic(@Body groceryItemIds: List<Int>): List<RecipeResponseDto>

    @GET("/api/recipes/public/category/{categoryId}")
    suspend fun getRecipesByCategoryPublic(@Path("categoryId") categoryId: Int): List<RecipeResponseDto>

    //С авторизацией

    @GET("/api/recipes")
    suspend fun getAllRecipesForUser(@Header("userId") userId: Int): List<RecipeResponseDto>

    @GET("/api/recipes/{id}")
    suspend fun getRecipeByIdForUser(
        @Path("id") id: Int,
        @Header("userId") userId: Int
    ): RecipeDetailResponseDto

    @GET("/api/recipes/favourites")
    suspend fun getFavouriteRecipes(@Header("userId") userId: Int): List<RecipeResponseDto>

    @POST("/api/recipes/{id}/like")
    suspend fun addLike(
        @Path("id") id: Int,
        @Header("userId") userId: Int
    ): Response<Unit>

    @DELETE("/api/recipes/{id}/like")
    suspend fun removeLike(
        @Path("id") id: Int,
        @Header("userId") userId: Int
    ): Response<Unit>

    @POST("/api/recipes/{id}/favourite")
    suspend fun addToFavourites(
        @Path("id") id: Int,
        @Header("userId") userId: Int
    ): Response<Unit>

    @DELETE("/api/recipes/{id}/favourite")
    suspend fun removeFromFavourites(
        @Path("id") id: Int,
        @Header("userId") userId: Int
    ): Response<Unit>

    @GET("/api/recipes/{id}/favourite")
    suspend fun isRecipeFavourite(
        @Path("id") id: Int,
        @Header("userId") userId: Int
    ): Response<Boolean>

    @GET("/api/recipes/{id}/like")
    suspend fun isRecipeLiked(
        @Path("id") id: Int,
        @Header("userId") userId: Int
    ): Response<Boolean>

    @GET("/api/recipes/{id}/likes_count")
    suspend fun getLikesCount(@Path("id") id: Int): Response<Int>

    @POST("/api/recipes/by-grocery-items")
    suspend fun getRecipesByGroceryItemsForUser(
        @Body groceryItemIds: List<Int>,
        @Header("userId") userId: Int
    ): List<RecipeResponseDto>

    @POST("/api/recipes/by-exact-grocery-items")
    suspend fun getRecipesByExactGroceryItemsForUser(
        @Body groceryItemIds: List<Int>,
        @Header("userId") userId: Int
    ): List<RecipeResponseDto>

    @GET("/api/recipes/search")
    suspend fun searchRecipesForUser(
        @Query("query") query: String,
        @Header("userId") userId: Int
    ): List<RecipeResponseDto>

    @GET("/api/recipes/category/{categoryId}")
    suspend fun getRecipesByCategoryForUser(
        @Path("categoryId") categoryId: Int,
        @Header("userId") userId: Int
    ): List<RecipeResponseDto>

    @GET("/api/groceries")
    suspend fun getAllGroceries(): List<GroceryDto>
}