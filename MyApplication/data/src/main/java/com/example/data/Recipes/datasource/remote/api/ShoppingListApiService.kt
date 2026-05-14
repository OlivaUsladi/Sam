package com.example.data.Recipes.datasource.remote.api

import com.example.data.Recipes.datasource.remote.dto.AddItemsFromRecipeRequestDto
import com.example.data.Recipes.datasource.remote.dto.AddShoppingListItemRequestDto
import com.example.data.Recipes.datasource.remote.dto.CheckAllItemsRequestDto
import com.example.data.Recipes.datasource.remote.dto.CreateShoppingListRequestDto
import com.example.data.Recipes.datasource.remote.dto.MergeShoppingListsRequestDto
import com.example.data.Recipes.datasource.remote.dto.RenameShoppingListRequestDto
import com.example.data.Recipes.datasource.remote.dto.ShoppingListItemResponseDto
import com.example.data.Recipes.datasource.remote.dto.ShoppingListResponseDto
import com.example.data.Recipes.datasource.remote.dto.UpdateShoppingListItemRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ShoppingListApiService {

    @GET("/api/shopping-lists")
    suspend fun getShoppingLists(@Header("userId") userId: Int): List<ShoppingListResponseDto>

    @GET("/api/shopping-lists/{listId}")
    suspend fun getShoppingListById(
        @Path("listId") listId: Int,
        @Header("userId") userId: Int
    ): ShoppingListResponseDto

    @POST("/api/shopping-lists")
    suspend fun createShoppingList(
        @Header("userId") userId: Int,
        @Body body: CreateShoppingListRequestDto
    ): ShoppingListResponseDto

    @PATCH("/api/shopping-lists/{listId}")
    suspend fun renameShoppingList(
        @Path("listId") listId: Int,
        @Header("userId") userId: Int,
        @Body body: RenameShoppingListRequestDto
    ): ShoppingListResponseDto

    @DELETE("/api/shopping-lists/{listId}")
    suspend fun deleteShoppingList(
        @Path("listId") listId: Int,
        @Header("userId") userId: Int
    ): Response<Unit>

    @POST("/api/shopping-lists/{listId}/items")
    suspend fun addItemToList(
        @Path("listId") listId: Int,
        @Header("userId") userId: Int,
        @Body body: AddShoppingListItemRequestDto
    ): ShoppingListItemResponseDto

    @POST("/api/shopping-lists/{listId}/items/from-recipe")
    suspend fun addItemsFromRecipe(
        @Path("listId") listId: Int,
        @Header("userId") userId: Int,
        @Body body: AddItemsFromRecipeRequestDto
    ): List<ShoppingListItemResponseDto>

    @PATCH("/api/shopping-lists/items/{itemId}")
    suspend fun updateItem(
        @Path("itemId") itemId: Int,
        @Header("userId") userId: Int,
        @Body body: UpdateShoppingListItemRequestDto
    ): ShoppingListItemResponseDto

    @DELETE("/api/shopping-lists/items/{itemId}")
    suspend fun deleteItem(
        @Path("itemId") itemId: Int,
        @Header("userId") userId: Int
    ): Response<Unit>

    @PATCH("/api/shopping-lists/{listId}/items/check-all")
    suspend fun checkAllItems(
        @Path("listId") listId: Int,
        @Header("userId") userId: Int,
        @Body body: CheckAllItemsRequestDto
    ): Response<Unit>

    @DELETE("/api/shopping-lists/{listId}/items/completed")
    suspend fun clearCompletedItems(
        @Path("listId") listId: Int,
        @Header("userId") userId: Int
    ): Response<Unit>

    @POST("/api/shopping-lists/merge")
    suspend fun mergeShoppingLists(
        @Header("userId") userId: Int,
        @Body body: MergeShoppingListsRequestDto
    ): ShoppingListResponseDto
}
